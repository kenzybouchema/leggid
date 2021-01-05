package com.kb.leggid.security;

import com.kb.leggid.exceptions.SpringRedditException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.time.Instant;
import java.util.Date;

import static java.util.Date.from;

@Service
public class JwtProvider {

    private KeyStore keyStore;
    @Value("${jwt.expiration.time}")
    private Long jwtExpirationInMillis;

    public String generateToken(Authentication authentication){
        // Ici on caste à USER au sens de spring
        User principal = (User) authentication.getPrincipal();
        // Pour un utilisateur on va lire la clé privé dans le Key store
        return Jwts.builder()
                .setSubject(principal.getUsername())
                .signWith(getPrivateKey())
                // Ajout de l'expiration
                .setExpiration(from(Instant.now().plusMillis(jwtExpirationInMillis)))
                .compact();
    }

    @PostConstruct
    public void init() {
        try {
            keyStore = KeyStore.getInstance("JKS");
            // On va lire le fichier qui contient le keyStore
            InputStream resourceAsStream = getClass().getResourceAsStream("/springblog.jks");
            // En arguments le keyStore et le mot de passe
            keyStore.load(resourceAsStream, "secret".toCharArray());
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | java.io.IOException e) {
            throw new SpringRedditException("Exception occurred while loading keystore", e);
        }
    }

    private PrivateKey getPrivateKey() {
        try {
            return (PrivateKey) keyStore.getKey("springblog", "secret".toCharArray());
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            throw new SpringRedditException("Exception occured while retrieving public key from keystore", e);
        }
    }

    public boolean validateToken(String jwt) {
        // Deprecated:
        // parser().setSigningKey(getPublickey()).parseClaimsJws(jwt);
        Jwts.parserBuilder()
                .setSigningKey(getPublickey())
                .build()
                .parseClaimsJws(jwt);
        // Si la clé est bien parsé alors le token est validé
        return true;
    }

    private PublicKey getPublickey() {
        try {
            return keyStore.getCertificate("springblog").getPublicKey();
        } catch (KeyStoreException e) {
            throw new SpringRedditException("Exception occured while " +
                    "retrieving public key from keystore", e);
        }
    }

    public String getUsernameFromJwt(String token){
        // Lire la claim depuis le token ( la claim est le body du token )
        Claims claims = Jwts.parserBuilder()
                //.requireAudience("string")
                .setSigningKey(getPublickey())
                .build().parseClaimsJws(token)
                .getBody();
        // Enfin dans la claim l'utilisateur est le sujet ( subject)
        return claims.getSubject();
    }

    public String generateTokenWithUserName(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(from(Instant.now()))
                .signWith(getPrivateKey())
                .setExpiration(from(Instant.now().plusMillis(jwtExpirationInMillis)))
                .compact();
    }

    public Long getJwtExpirationInMillis() {
        return jwtExpirationInMillis;
    }
}

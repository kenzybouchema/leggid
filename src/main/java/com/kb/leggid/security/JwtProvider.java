package com.kb.leggid.security;

import com.kb.leggid.exceptions.SpringRedditException;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;

@Service
public class JwtProvider {

    private KeyStore keyStore;

    public String generateToken(Authentication authentication){
        // Ici on caste à USER au sens de spring
        User principal = (User) authentication.getPrincipal();
        // Pour un utilisateur on va lire la clé privé dans le Key store
        return Jwts.builder()
                .setSubject(principal.getUsername())
                .signWith(getPrivateKey())
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
}

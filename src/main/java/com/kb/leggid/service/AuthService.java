package com.kb.leggid.service;

import com.kb.leggid.dto.AuthenticationResponse;
import com.kb.leggid.dto.LoginRequest;
import com.kb.leggid.dto.RefreshTokenRequest;
import com.kb.leggid.dto.RegisterRequest;
import com.kb.leggid.exceptions.SpringRedditException;
import com.kb.leggid.model.NotificationEmail;
import com.kb.leggid.model.User;
import com.kb.leggid.model.VerificationToken;
import com.kb.leggid.repository.UserRepository;
import com.kb.leggid.repository.VerificationTokenRepository;
import com.kb.leggid.security.JwtProvider;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static java.util.Date.from;

@Service
@AllArgsConstructor // Lombok génère le constructeur avec tout les args
public class AuthService {

    // Injection par constructeur :
    // Ici le constructeur est générer par Lombok !

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    private final VerificationTokenRepository verificationTokenRepository;

    private final MailService mailService;

    private final AuthenticationManager authenticationManager; // Une interface, il faut un bean !

    private final JwtProvider jwtProvider;

    private final RefreshTokenService refreshTokenService;

    private final MailContentBuilder mailContentBuilder;

    @Transactional
    // Si au niveau de la classe toute les méthodes sont transactionnelles,// ici on préfère le niveau méthode
    public void signup(RegisterRequest registerRequest) {
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setCreated(Instant.now());
        user.setEnabled(false);// Tant que l'utilisateur n'est pas validé par m
        userRepository.save(user); //TODO: emmpêcher la création de doublon

        String token = generateVerificationToken(user);

        mailService.sendMail(new NotificationEmail(
                "Please Activate your Account",
                user.getEmail(),
                // Dans le corps du message on a le lien vers l'application avec le token que l'on vient d'envoyer.
                // L'utilisateur en cliquant dessus va appeler la méthode mappé
                // sur "/accountVerification/" dans le le controller mappé sur "/api/auth/"
                // avec comme variable dans la requête le token, afin de verifier l"existence en base d'un couple
                // user/token !
                mailContentBuilder.build("Thank you for signing up to Spring Reddit, " +
                "please click on the below url to activate your account : " +
                "http://localhost:8080/api/auth/accountVerification/" + token)));
    }

    /**
     * Crée un token pour l'utilisateur enregistre le couple dans la base de données
     *  renvoie le token qui doit être envoyé par mail par la suite
     * @param user
     * @return
     */
    private String generateVerificationToken(User user) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationTokenRepository.save(verificationToken);
        return token;
    }

    public void verifyAccount(String token) {
        Optional<VerificationToken> verificationToken = verificationTokenRepository.findByToken(token);
        fetchUserAndEnable(verificationToken.orElseThrow(() -> new SpringRedditException("Invalid Token")));

    }

    @Transactional
    void fetchUserAndEnable(VerificationToken verificationToken) {
        String username = verificationToken.getUser().getUsername();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new SpringRedditException("User not found with name - " + username));
        user.setEnabled(true);
        userRepository.save(user);
    }

    public AuthenticationResponse login(LoginRequest loginRequest) {
        Authentication authenticate = authenticationManager.authenticate(
                // Construit à partir du DTO l'authentification
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        // Dans le context du fil d'execution
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        // Fournit un token pour l'utilisateur authentifié dans "authenticate"
        String token = jwtProvider.generateToken(authenticate);
        // Retoune le token avec l'utilisateur
        return AuthenticationResponse.builder()
                .authenticationToken(token)
                .refreshToken(refreshTokenService.generateRefreshToken().getToken())
                .expiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()))
                .username(loginRequest.getUsername())
                .build();
    }

    @Transactional(readOnly = true)
    public User getCurrentUser() {
        org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User) SecurityContextHolder.
                getContext().getAuthentication().getPrincipal();
        return userRepository.findByUsername(principal.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User name not found - " + principal.getUsername()));
    }

    public boolean isLoggedIn() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return !(authentication instanceof AnonymousAuthenticationToken) && authentication.isAuthenticated();
    }


    public AuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        refreshTokenService.validateRefreshToken(refreshTokenRequest.getRefreshToken());
        String refreshedToken = jwtProvider.generateTokenWithUserName(refreshTokenRequest.getUserName());
        return AuthenticationResponse.builder()
                .authenticationToken(refreshedToken)
                .refreshToken(refreshTokenRequest.getRefreshToken())
                .expiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()))
                .username(refreshTokenRequest.getUserName())
                .build();
    }
}

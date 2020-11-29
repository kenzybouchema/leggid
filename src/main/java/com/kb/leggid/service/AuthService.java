package com.kb.leggid.service;

import com.kb.leggid.dto.RegisterRequest;
import com.kb.leggid.exceptions.SpringRedditException;
import com.kb.leggid.model.NotificationEmail;
import com.kb.leggid.model.User;
import com.kb.leggid.model.VerificationToken;
import com.kb.leggid.repository.UserRepository;
import com.kb.leggid.repository.VerificationTokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor // Lombok génère le constructeur avec tout les args
public class AuthService {

    // Injection par constructeur :
    // Ici le contrsucteur est générer par Lombok !

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    private final VerificationTokenRepository verificationTokenRepository;

    private final MailService mailService;

    @Transactional
    // Si au niveau de la classe toute les émthodes sont transactionnelles,// ici on préfère le niveau méthode
    public void signup(RegisterRequest registerRequest) {
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setCreated(Instant.now());
        user.setEnabled(false);// Tant que l'utilisateur n'est pas validé par m
        userRepository.save(user);

        String token = generateVerificationToken(user);

        mailService.sendMail(new NotificationEmail(
                "Please Activate your Account",
                user.getEmail(),
                // Dans le corps du message on a le lien vers l'application avec le token que l'on vient d'envoyer.
                // L'utilisateur en cliquant dessus va appeler la méthode mappé
                // sur "/accountVerification/" dans le lecontroller mappé sur "/api/auth/"
                // avec comme variable dans la requete le token, afin de verifier l"existance en base d'un couple
                // user/token !
                "Thank you for signing up to Spring Reddit, " +
                "please click on the below url to activate your account : " +
                "http://localhost:8080/api/auth/accountVerification/" + token));
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
    private void fetchUserAndEnable(VerificationToken verificationToken) {
        String username = verificationToken.getUser().getUsername();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new SpringRedditException("User not found with name - " + username));
        user.setEnabled(true);
        userRepository.save(user);
    }
}

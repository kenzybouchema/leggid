package com.kb.leggid.service;

import com.kb.leggid.dto.RegisterRequest;
import com.kb.leggid.model.User;
import com.kb.leggid.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@AllArgsConstructor // Lombok génère le constructeur avec tout les args
public class AuthService {

    // Injection par constructeur :
    // Ici le contrsucteur est générer par Lombok !

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

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
    }
}

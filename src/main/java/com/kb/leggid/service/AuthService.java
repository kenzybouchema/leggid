package com.kb.leggid.service;

import com.kb.leggid.dto.RegisterRequest;
import com.kb.leggid.model.User;
import com.kb.leggid.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AuthService {

    // Note sur l'injéction par champs :
    // Spring recommande l'injection par constructeur

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

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

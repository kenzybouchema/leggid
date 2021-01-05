package com.kb.leggid.service;

import com.kb.leggid.exceptions.SpringRedditException;
import com.kb.leggid.model.RefreshToken;
import com.kb.leggid.repository.RefreshTokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;


    /**
     * Génère un refresh token et l'enregistre en base de donnée
     * @return Le refresh token
     */
    @Transactional
    public RefreshToken generateRefreshToken() {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setCreatedDate(Instant.now());
        return refreshTokenRepository.save(refreshToken);
    }

    /**
     * Cherche dans la base de données si un refresh token existe pour le JWT donné
     * @param token le JWT Token
     */
    @Transactional(readOnly = true)
    void validateRefreshToken(String token) {
        refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new SpringRedditException("Invalid refresh Token"));
    }

    /**
     * Simple suppression du refresh token a partir du JWT
     * @param token le JWT Token
     */
    @Transactional
    public void deleteRefreshToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }
}

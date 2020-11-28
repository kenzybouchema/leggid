package com.kb.leggid.model;

import com.kb.leggid.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

/**
 * Lors de la création d'un User un token est persisté en base de donné et
 *  utilisé pour que l'utilisateur puisse valider son inscription ( on vérifie que le token est
 *  bien lié à l'utilisateur
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "token") // La table qui sotocke cette entité sera le nommé "token"
public class VerificationToken {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private String token;
    @OneToOne(fetch = LAZY) // Un token est lié uniquement à un seul user.
    private User user;
    private Instant expiryDate;
}

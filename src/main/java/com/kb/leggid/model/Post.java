package com.kb.leggid.model;

import javax.persistence.Entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.Instant;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Data //Lombok <=> @getter + @Setter + @RequiredArgsCosntructor + @ToString + @EqualsAndHashCode
// Va génerer :
// des getters pour tout les champs
// des setters pour les champs non finaux
// un constructeur avec tout les champs @NonNull pour arguments
// les méthodes toString()
// hashCode() et equals pour tout les champs non transient ( c'est à dire ceux qui seront persistés !! )
@Entity // La classe sera mappé avec une entité du même nom
@Builder // Génère un builder  avec tout les champs de la classe !
@AllArgsConstructor // Génère un constructeur avec tout les champs pour arguments
@NoArgsConstructor // Génère un constructeurs sans arguments
public class Post {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long postId;
    @NotBlank(message = "Post Name cannot be empty or Null")
    private String postName;
    @Nullable
    private String url;
    @Nullable
    @Lob // Large Object : peut contenir du texte
    private String description;
    private Integer voteCount = 0;
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "userId", referencedColumnName = "userId")
    private User user;
    private Instant createdDate;
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "id", referencedColumnName = "id")
    private SubLeggid subreddit;
}

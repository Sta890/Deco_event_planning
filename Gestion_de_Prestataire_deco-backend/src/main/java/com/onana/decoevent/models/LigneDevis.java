package com.onana.decoevent.models;



import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lignes_devis")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LigneDevis extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "devis_id", nullable = false)
    private Devis devis;

    @ManyToOne
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    @Column(nullable = false)
    private Integer quantite;

    @Column(nullable = false)
    private Double prixUnitaire;

    @Column(nullable = false)
    private Double sousTotal;
}

package com.onana.decoevent.models;

import com.onana.decoevent.enums.TypeEvenement;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "articles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Article extends BaseEntity {

    @Column(nullable = false)
    private String nom;

    private String description;

    @Column(nullable = false)
    private Double prixUnitaire;
    @Enumerated(EnumType.STRING)
    private TypeEvenement typeEvenement;


}
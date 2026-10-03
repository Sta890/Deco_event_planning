package com.onana.decoevent.models;



import com.onana.decoevent.enums.TypeEvenement;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "prestations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prestation extends BaseEntity {


    @Column(nullable = false)
    private LocalDate dateEvenement;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeEvenement typeEvenement;

    @Column(nullable = false)
    private String lieu;


    private String description;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @OneToMany(mappedBy = "prestation", cascade = CascadeType.ALL)
    private List<Devis> devis;
}
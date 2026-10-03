package com.onana.decoevent.models;

import com.onana.decoevent.enums.StatutDevis;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "devis")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Devis extends BaseEntity {

    @Column(nullable = false)
    private LocalDate dateCreation;


    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal montantTotal;

    @ManyToOne
    @JoinColumn(name = "prestation_id", nullable = false)
    private Prestation prestation;

    @OneToMany(mappedBy = "devis", cascade = CascadeType.ALL)
    private List<LigneDevis> lignes;

    @OneToOne(mappedBy = "devis", cascade = CascadeType.ALL)
    private Facture facture;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutDevis statutDevis;
}
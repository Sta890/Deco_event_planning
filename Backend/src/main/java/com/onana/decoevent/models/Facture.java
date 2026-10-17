package com.onana.decoevent.models;
import com.onana.decoevent.enums.StatutFacture;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "factures")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Facture extends BaseEntity {

    @Column(nullable = false)
    private LocalDate dateFacture;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal montantTotal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutFacture statutFacture;

    @OneToOne
    @JoinColumn(name = "devis_id", nullable = false)
    private Devis devis;

    @OneToOne(mappedBy = "facture", cascade = CascadeType.ALL)
    private Paiement paiement;


}
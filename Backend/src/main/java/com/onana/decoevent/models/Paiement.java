package com.onana.decoevent.models;



import com.onana.decoevent.enums.ModePaiement;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "paiements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Paiement extends BaseEntity {

    @Column(nullable = false)
    private LocalDate datePaiement;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal montant;

    @OneToOne
    @JoinColumn(name = "facture_id", nullable = false)
    private Facture facture;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ModePaiement modePaiement;


}

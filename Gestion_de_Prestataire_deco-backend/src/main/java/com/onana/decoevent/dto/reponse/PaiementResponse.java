package com.onana.decoevent.dto.reponse;



import com.onana.decoevent.enums.ModePaiement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaiementResponse {
    private Long id;
    private LocalDate datePaiement;
    private Double montant;
    private ModePaiement modePaiement;
    private Long factureId;
    private String clientNom;
    private LocalDateTime createdAt;
}

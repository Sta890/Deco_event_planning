package com.onana.decoevent.dto.response;

import com.onana.decoevent.enums.StatutDevis;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DevisResponse {
    private Long id;
    private LocalDate dateCreation;
    private StatutDevis statutDevis;
    private BigDecimal montantTotal;
    private Long prestationId;
    private String typeEvenement;
    private String clientNom;
    private List<LigneDevisResponse> lignes;
    private LocalDateTime createdAt;
}

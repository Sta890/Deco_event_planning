package com.onana.decoevent.dto.response;

import com.onana.decoevent.enums.StatutFacture;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FactureResponse {
    private Long id;
    private LocalDate dateFacture;
    private BigDecimal montantTotal;
    private StatutFacture statutFacture;
    private Long devisId;
    private String clientNom;
    private String typeEvenement;
    private LocalDateTime createdAt;
}

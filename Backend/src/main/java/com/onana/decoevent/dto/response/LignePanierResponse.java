package com.onana.decoevent.dto.response;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LignePanierResponse {
    private Long id;
    private Long articleId;
    private String articleNom;
    private BigDecimal prixUnitaire;
    private Integer quantite;
    private BigDecimal sousTotal;
    private String typeEvenement;
}

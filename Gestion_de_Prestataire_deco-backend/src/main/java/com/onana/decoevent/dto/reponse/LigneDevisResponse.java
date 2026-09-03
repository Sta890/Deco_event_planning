package com.onana.decoevent.dto.reponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LigneDevisResponse {
    private Long id;
    private Long articleId;
    private String articleNom;
    private Integer quantite;
    private Double prixUnitaire;
    private Double sousTotal;
}

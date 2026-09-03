package com.onana.decoevent.dto.reponse;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LignePanierResponse {
    private Long id;
    private Long articleId;
    private String articleNom;
    private Double prixUnitaire;
    private Integer quantite;
    private Double sousTotal;
    private String typeEvenement;
}

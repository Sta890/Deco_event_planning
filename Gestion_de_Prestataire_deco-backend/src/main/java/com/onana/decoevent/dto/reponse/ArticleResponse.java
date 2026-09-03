package com.onana.decoevent.dto.reponse;



import com.onana.decoevent.enums.TypeEvenement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleResponse {
    private Long id;
    private String nom;
    private String description;
    private Double prixUnitaire;
    private TypeEvenement typeEvenement;
}

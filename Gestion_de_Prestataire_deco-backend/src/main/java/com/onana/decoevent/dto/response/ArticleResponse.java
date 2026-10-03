package com.onana.decoevent.dto.response;



import com.onana.decoevent.enums.TypeEvenement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleResponse {
    private Long id;
    private String nom;
    private String description;
    private BigDecimal prixUnitaire;
    private TypeEvenement typeEvenement;
}

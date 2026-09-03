package com.onana.decoevent.dto.request;

import com.onana.decoevent.enums.TypeEvenement;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ArticleRequest {

    @NotBlank(message = "Nom obligatoire")
    private String nom;

    private String description;

    @NotNull(message = "Prix unitaire obligatoire")
    private Double prixUnitaire;

    @NotNull(message = "Type événement obligatoire")
    private TypeEvenement typeEvenement;
}

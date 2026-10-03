package com.onana.decoevent.dto.request;

import com.onana.decoevent.enums.TypeEvenement;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ArticleRequest {

    @NotBlank(message = "Nom obligatoire")
    private String nom;

    private String description;

    @NotNull(message = "Prix unitaire obligatoire")
    @DecimalMin(value = "0.0", inclusive = false, message = "Prix unitaire doit être positif")
    private BigDecimal prixUnitaire;

    @NotNull(message = "Type événement obligatoire")
    private TypeEvenement typeEvenement;
}

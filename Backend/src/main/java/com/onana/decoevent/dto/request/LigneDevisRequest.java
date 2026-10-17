package com.onana.decoevent.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class LigneDevisRequest {

    @NotNull(message = "Article obligatoire")
    private Long articleId;

    @NotNull(message = "Quantité obligatoire")
    @Positive(message = "Quantité doit être positive")
    private Integer quantite;
}

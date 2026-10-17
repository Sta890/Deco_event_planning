package com.onana.decoevent.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class DevisRequest {

    @NotNull(message = "Prestation obligatoire")
    private Long prestationId;

    @NotNull(message = "Lignes obligatoires")
    private List<LigneDevisRequest> lignes;
}

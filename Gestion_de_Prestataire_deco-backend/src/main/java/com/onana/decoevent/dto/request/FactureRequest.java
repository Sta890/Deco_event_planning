package com.onana.decoevent.dto.request;

import com.onana.decoevent.enums.StatutFacture;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FactureRequest {

    @NotNull(message = "Devis obligatoire")
    private Long devisId;

    @NotNull(message = "Statut obligatoire")
    private StatutFacture statut;
}

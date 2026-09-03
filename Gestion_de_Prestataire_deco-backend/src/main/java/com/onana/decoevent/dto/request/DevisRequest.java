package com.onana.decoevent.dto.request;

import com.onana.decoevent.enums.StatutDevis;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class DevisRequest {

    @NotNull(message = "Prestation obligatoire")
    private Long prestationId;

    @NotNull(message = "Statut obligatoire")
    private StatutDevis statut;

    @NotNull(message = "Lignes obligatoires")
    private List<LigneDevisRequest> lignes;
}

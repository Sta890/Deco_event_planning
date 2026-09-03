package com.onana.decoevent.dto.request;

import com.onana.decoevent.enums.ModePaiement;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PaiementRequest {

    @NotNull(message = "Facture obligatoire")
    private Long factureId;

    @NotNull(message = "Montant obligatoire")
    @Positive(message = "Montant doit être positif")
    private Double montant;

    @NotNull(message = "Mode paiement obligatoire")
    private ModePaiement modePaiement;

    @NotNull(message = "Date paiement obligatoire")
    private LocalDate datePaiement;
}

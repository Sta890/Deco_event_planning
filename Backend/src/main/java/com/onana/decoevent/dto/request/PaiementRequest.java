package com.onana.decoevent.dto.request;

import com.onana.decoevent.enums.ModePaiement;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PaiementRequest {

    @NotNull(message = "Facture obligatoire")
    private Long factureId;

    @NotNull(message = "Montant obligatoire")
    @DecimalMin(value = "0.0", inclusive = false, message = "Montant doit être positif")
    @Digits(integer = 17, fraction = 2, message = "Montant : maximum 2 décimales")
    private BigDecimal montant;

    @NotNull(message = "Mode paiement obligatoire")
    private ModePaiement modePaiement;

    @NotNull(message = "Date paiement obligatoire")
    private LocalDate datePaiement;
}

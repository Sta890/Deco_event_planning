package com.onana.decoevent.dto.request;


import com.onana.decoevent.enums.TypeEvenement;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PrestationRequest {

    @NotNull(message = "Type événement obligatoire")
    private TypeEvenement typeEvenement;

    @NotNull(message = "Date événement obligatoire")
    private LocalDate dateEvenement;

    @NotBlank(message = "Lieu obligatoire")
    private String lieu;

    private String description;

    @NotNull(message = "Client obligatoire")
    private Long clientId;
}
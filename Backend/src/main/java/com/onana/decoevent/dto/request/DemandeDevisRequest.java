package com.onana.decoevent.dto.request;

import com.onana.decoevent.enums.TypeEvenement;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class DemandeDevisRequest {

    @NotBlank(message = "Nom obligatoire")
    private String nom;

    @NotBlank(message = "Téléphone obligatoire")
    private String telephone;

    @NotBlank(message = "Email obligatoire")
    @Email(message = "Email invalide")
    private String email;

    @NotNull(message = "Type événement obligatoire")
    private TypeEvenement typeEvenement;

    @NotNull(message = "Date événement obligatoire")
    private LocalDate dateEvenement;

    @NotBlank(message = "Lieu obligatoire")
    private String lieu;

    private Integer nombrePersonnes;

    private String message;
}

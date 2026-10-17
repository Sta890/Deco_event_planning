package com.onana.decoevent.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class InscriptionRequest {

    @NotBlank(message = "Nom obligatoire")
    private String nom;

    @NotBlank(message = "Email obligatoire")
    @Email(message = "Email invalide")
    private String email;

    @NotBlank(message = "Mot de passe obligatoire")
    @Size(min = 4, message = "Mot de passe doit contenir au moins 4 caractères")
    private String motDePasse;

    @NotBlank(message = "Téléphone obligatoire")
    private String telephone;

    private String adresse;
}

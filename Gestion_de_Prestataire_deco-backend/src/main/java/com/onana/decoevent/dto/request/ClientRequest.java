package com.onana.decoevent.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ClientRequest {

    @NotBlank(message = "Nom obligatoire")
    private String nom;

    @NotBlank(message = "Téléphone obligatoire")
    private String telephone;

    private String adresse;

    @Email(message = "Email invalide")
    private String email;
}

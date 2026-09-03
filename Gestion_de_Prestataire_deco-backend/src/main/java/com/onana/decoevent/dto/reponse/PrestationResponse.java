package com.onana.decoevent.dto.reponse;



import com.onana.decoevent.enums.TypeEvenement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrestationResponse {
    private Long id;
    private TypeEvenement typeEvenement;
    private LocalDate dateEvenement;
    private String lieu;
    private String description;
    private Long clientId;
    private String clientNom;
    private LocalDateTime createdAt;
}

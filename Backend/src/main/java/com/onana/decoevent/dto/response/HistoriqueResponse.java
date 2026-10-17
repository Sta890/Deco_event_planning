package com.onana.decoevent.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoriqueResponse {
    private Long id;
    private String action;
    private LocalDateTime dateAction;
    private String utilisateurNom;
}

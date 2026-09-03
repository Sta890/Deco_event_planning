package com.onana.decoevent.dto.reponse;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PanierResponse {
    private Long id;
    private Long utilisateurId;
    private List<LignePanierResponse> lignes;
    private Double total;
}

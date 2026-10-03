package com.onana.decoevent.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PanierResponse {
    private Long id;
    private Long utilisateurId;
    private List<LignePanierResponse> lignes;
    private BigDecimal total;
}

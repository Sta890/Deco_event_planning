package com.onana.decoevent.dto.response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {
    private Long totalClients;
    private Long totalPrestations;
    private Long devisEnAttente;
    private Long facturesEnRetard;
    private BigDecimal chiffreAffaires;
    private Long tauxConversion;
}

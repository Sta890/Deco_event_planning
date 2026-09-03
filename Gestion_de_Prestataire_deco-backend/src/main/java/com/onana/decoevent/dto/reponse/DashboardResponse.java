package com.onana.decoevent.dto.reponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {
    private Long totalClients;
    private Long totalPrestations;
    private Long devisEnAttente;
    private Long facturesEnRetard;
    private Double chiffreAffaires;
    private Long tauxConversion;
}

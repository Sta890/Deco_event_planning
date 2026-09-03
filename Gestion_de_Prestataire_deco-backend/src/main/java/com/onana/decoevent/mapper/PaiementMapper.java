package com.onana.decoevent.mapper;

import com.onana.decoevent.dto.reponse.PaiementResponse;
import com.onana.decoevent.dto.request.PaiementRequest;
import com.onana.decoevent.models.Facture;
import com.onana.decoevent.models.Paiement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PaiementMapper {

    @Mapping(source = "facture.id", target = "factureId")
    @Mapping(source = "facture.devis.prestation.client.nom", target = "clientNom")
    PaiementResponse toResponse(Paiement paiement);

    List<PaiementResponse> toResponseList(List<Paiement> paiements);

    Paiement toEntity(PaiementRequest dto, Facture facture);
}
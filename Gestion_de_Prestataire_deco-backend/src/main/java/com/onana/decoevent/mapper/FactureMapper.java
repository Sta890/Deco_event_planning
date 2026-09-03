package com.onana.decoevent.mapper;
import com.onana.decoevent.dto.reponse.FactureResponse;
import com.onana.decoevent.models.Facture;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FactureMapper {

    @Mapping(source = "devis.id", target = "devisId")
    @Mapping(source = "devis.prestation.client.nom", target = "clientNom")
    @Mapping(source = "devis.prestation.typeEvenement", target = "typeEvenement")
    FactureResponse toResponse(Facture facture);

    List<FactureResponse> toResponseList(List<Facture> factures);
}
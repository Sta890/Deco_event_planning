package com.onana.decoevent.mapper;
import com.onana.decoevent.dto.response.PrestationResponse;
import com.onana.decoevent.dto.request.PrestationRequest;
import com.onana.decoevent.models.Client;
import com.onana.decoevent.models.Prestation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PrestationMapper {

    @Mapping(source = "request.typeEvenement", target = "typeEvenement")
    @Mapping(source = "request.dateEvenement", target = "dateEvenement")
    @Mapping(source = "request.lieu", target = "lieu")
    @Mapping(source = "request.description", target = "description")
    @Mapping(source = "client", target = "client")
    @Mapping(target = "devis", ignore = true)
    Prestation toEntity(PrestationRequest request, Client client);

    @Mapping(source = "client.id", target = "clientId")
    @Mapping(source = "client.nom", target = "clientNom")
    PrestationResponse toResponse(Prestation prestation);

    List<PrestationResponse> toResponseList(List<Prestation> prestations);

    @Mapping(source = "request.typeEvenement", target = "typeEvenement")
    @Mapping(source = "request.dateEvenement", target = "dateEvenement")
    @Mapping(source = "request.lieu", target = "lieu")
    @Mapping(source = "request.description", target = "description")
    @Mapping(target = "devis", ignore = true)
    @Mapping(target = "client", ignore = true)
    Prestation updateEntityFromDto(PrestationRequest request, @MappingTarget Prestation prestation);
}
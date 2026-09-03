package com.onana.decoevent.mapper;
import com.onana.decoevent.dto.reponse.ClientResponse;
import com.onana.decoevent.dto.request.ClientRequest;
import com.onana.decoevent.models.Client;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ClientMapper {

    @Mapping(target = "prestations", ignore = true)
    Client toEntity(ClientRequest request);

    ClientResponse toResponse(Client client);

    List<ClientResponse> toResponseList(List<Client> clients);

    @Mapping(source = "request.nom", target = "nom")
    @Mapping(source = "request.telephone", target = "telephone")
    @Mapping(source = "request.adresse", target = "adresse")
    @Mapping(source = "request.email", target = "email")
    @Mapping(target = "prestations", ignore = true)
    Client updateEntityFromDto(ClientRequest request, @MappingTarget Client client);
}
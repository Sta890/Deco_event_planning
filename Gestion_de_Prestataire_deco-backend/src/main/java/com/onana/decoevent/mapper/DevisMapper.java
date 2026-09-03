package com.onana.decoevent.mapper;
import com.onana.decoevent.dto.reponse.DevisResponse;
import com.onana.decoevent.models.Devis;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {LigneDevisMapper.class})
public interface DevisMapper {

    @Mapping(source = "prestation.id", target = "prestationId")
    @Mapping(source = "prestation.typeEvenement", target = "typeEvenement")
    @Mapping(source = "prestation.client.nom", target = "clientNom")
    @Mapping(source = "lignes", target = "lignes")
    DevisResponse toResponse(Devis devis);

    List<DevisResponse> toResponseList(List<Devis> devis);
}
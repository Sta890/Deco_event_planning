package com.onana.decoevent.mapper;
import com.onana.decoevent.dto.response.HistoriqueResponse;
import com.onana.decoevent.models.Historique;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface HistoriqueMapper {

    @Mapping(source = "utilisateur.nom", target = "utilisateurNom")
    HistoriqueResponse toResponse(Historique historique);

    List<HistoriqueResponse> toResponseList(List<Historique> historiques);
    @Mapping(target = "utilisateur", ignore = true)
    Historique toEntity(String action, java.time.LocalDateTime dateAction);
}
package com.onana.decoevent.mapper;
import com.onana.decoevent.dto.reponse.AuthResponse;
import com.onana.decoevent.dto.request.InscriptionRequest;
import com.onana.decoevent.models.Utilisateur;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UtilisateurMapper {

    @Mapping(target = "role", ignore = true)
    Utilisateur toEntity(InscriptionRequest request);

    @Mapping(target = "token", ignore = true)
    @Mapping(source = "id", target = "id")
    AuthResponse toAuthResponse(Utilisateur utilisateur);

}
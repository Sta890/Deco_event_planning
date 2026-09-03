package com.onana.decoevent.mapper;

import com.onana.decoevent.dto.reponse.LignePanierResponse;
import com.onana.decoevent.dto.reponse.PanierResponse;
import com.onana.decoevent.models.LignePanier;
import com.onana.decoevent.models.Panier;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")
public interface PanierMapper {

    @Mapping(source = "article.id", target = "articleId")
    @Mapping(source = "article.nom", target = "articleNom")
    @Mapping(source = "article.prixUnitaire", target = "prixUnitaire")
    @Mapping(source = "article.typeEvenement", target = "typeEvenement")
    LignePanierResponse toLignePanierResponse(LignePanier ligne);

    @Mapping(source = "utilisateur.id", target = "utilisateurId")
    @Mapping(source = "lignes", target = "lignes")
    @Mapping(target = "total", ignore = true)
    PanierResponse toPanierResponse(Panier panier);
    @AfterMapping
    default void calculerTotal(Panier panier, @MappingTarget PanierResponse response) {
        if (response.getLignes() != null) {
            double total = response.getLignes().stream()
                    .mapToDouble(LignePanierResponse::getSousTotal)
                    .sum();
            response.setTotal(total);
        } else {
            response.setTotal(0.0);
        }
    }
}
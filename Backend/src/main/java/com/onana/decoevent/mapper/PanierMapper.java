package com.onana.decoevent.mapper;

import com.onana.decoevent.dto.response.LignePanierResponse;
import com.onana.decoevent.dto.response.PanierResponse;
import com.onana.decoevent.models.LignePanier;
import com.onana.decoevent.models.Panier;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


import java.math.BigDecimal;
import java.util.Objects;


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
            BigDecimal total = response.getLignes().stream()
                    .map(LignePanierResponse::getSousTotal)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            response.setTotal(total);
        } else {
            response.setTotal(BigDecimal.ZERO);
        }
    }
}
package com.onana.decoevent.mapper;
import com.onana.decoevent.dto.reponse.LigneDevisResponse;
import com.onana.decoevent.models.Article;
import com.onana.decoevent.models.Devis;
import com.onana.decoevent.models.LigneDevis;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LigneDevisMapper {

    @Mapping(source = "article.id", target = "articleId")
    @Mapping(source = "article.nom", target = "articleNom")
    LigneDevisResponse toResponse(LigneDevis ligne);

    List<LigneDevisResponse> toResponseList(List<LigneDevis> lignes);
    @Mapping(source = "article", target = "article")
    @Mapping(source = "devis", target = "devis")
    @Mapping(source = "quantite", target = "quantite")
    @Mapping(target = "prixUnitaire", expression = "java(article.getPrixUnitaire())")
    @Mapping(target = "sousTotal", expression = "java(article.getPrixUnitaire() * quantite)")
    LigneDevis toEntity(Article article, Devis devis, Integer quantite);
}
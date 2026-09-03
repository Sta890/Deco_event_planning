package com.onana.decoevent.mapper;
import com.onana.decoevent.dto.reponse.ArticleResponse;
import com.onana.decoevent.dto.request.ArticleRequest;
import com.onana.decoevent.models.Article;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ArticleMapper {

    Article toEntity(ArticleRequest request);

    ArticleResponse toResponse(Article article);

    List<ArticleResponse> toResponseList(List<Article> articles);

    @Mapping(source = "request.nom", target = "nom")
    @Mapping(source = "request.description", target = "description")
    @Mapping(source = "request.prixUnitaire", target = "prixUnitaire")
    @Mapping(source = "request.typeEvenement", target = "typeEvenement")
    Article updateEntityFromDto(ArticleRequest request, @MappingTarget Article article);
}
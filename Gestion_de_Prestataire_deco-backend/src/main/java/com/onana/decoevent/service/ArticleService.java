package com.onana.decoevent.service;
import com.onana.decoevent.dto.reponse.ArticleResponse;
import com.onana.decoevent.dto.request.ArticleRequest;
import com.onana.decoevent.enums.TypeEvenement;
import com.onana.decoevent.exceptions.ResourceNotFoundException;
import com.onana.decoevent.mapper.ArticleMapper;
import com.onana.decoevent.models.Article;
import com.onana.decoevent.repostories.ArticleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final ArticleMapper articleMapper;
    private final HistoriqueService historiqueService;

    @Transactional(readOnly = true)
    public List<ArticleResponse> findAll() {
        log.info("Récupération de tous les articles");
        return articleMapper.toResponseList(articleRepository.findAll());
    }

    @Transactional(readOnly = true)
    public ArticleResponse findById(Long id) {
        log.info("Récupération de l'article avec l'id : {}", id);
        return articleMapper.toResponse(
                articleRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Article non trouvé avec l'id : " + id))
        );
    }

    @Transactional
    public ArticleResponse create(ArticleRequest dto) {
        log.info("Création article - Nom: {}", dto.getNom());

        Article article = articleMapper.toEntity(dto);
        article = articleRepository.save(article);

        log.info("Article créé avec succès - ID: {}, Nom: {}", article.getId(), article.getNom());
        historiqueService.enregistrer("Nouvel article ajouté : " + article.getNom());
        return articleMapper.toResponse(article);
    }

    @Transactional
    public ArticleResponse update(Long id, ArticleRequest dto) {
        log.info("Mise à jour article - ID: {}", id);
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article non trouvé avec l'id : " + id));

        articleMapper.updateEntityFromDto(dto, article);
        article = articleRepository.save(article);

        log.info("Article mis à jour avec succès - ID: {}, Nom: {}", article.getId(), article.getNom());
        historiqueService.enregistrer("Article modifié : " + article.getNom());
        return articleMapper.toResponse(article);
    }

    @Transactional
    public void delete(Long id) {
        log.info("Suppression article - ID: {}", id);
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article non trouvé avec l'id : " + id));

        historiqueService.enregistrer("Article supprimé : " + article.getNom());
        articleRepository.deleteById(id);
        log.info("Article supprimé avec succès - ID: {}", id);
    }

    @Transactional(readOnly = true)
    public List<ArticleResponse> findByTypeEvenement(TypeEvenement typeEvenement) {
        log.info("Récupération des articles par type événement : {}", typeEvenement);
        return articleMapper.toResponseList(articleRepository.findByTypeEvenement(typeEvenement));
    }
}
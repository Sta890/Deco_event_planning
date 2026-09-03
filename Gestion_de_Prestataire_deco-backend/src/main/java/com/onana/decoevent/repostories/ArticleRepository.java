package com.onana.decoevent.repostories;


import com.onana.decoevent.models.Article;
import com.onana.decoevent.enums.TypeEvenement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    List<Article> findByTypeEvenement(TypeEvenement typeEvenement);
    List<Article> findByNomContainingIgnoreCase(String nom);
}
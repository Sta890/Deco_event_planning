import { Component, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { CatalogueApiService } from '../../../services/api/catalogue-api';

@Component({
  selector: 'app-accueil',
  standalone: true,
  imports: [CommonModule, RouterModule, ButtonModule],
  templateUrl: './accueil.html',
  styleUrl: './accueil.css'
})
export class AccueilComponent implements OnInit {

  // ✅ Icônes gardées en local car c'est juste de la présentation
  services = [
    {
      icon: '💍',
      titre: 'Mariage',
      typeEvenement: 'MARIAGE',
      description: 'Décoration complète pour votre jour J. Arches florales, tables, chaises et bien plus.',
      nbArticles: signal(0)
    },
    {
      icon: '🍼',
      titre: 'Baptême',
      typeEvenement: 'BAPTEME',
      description: 'Un cadre féerique pour célébrer l\'arrivée de votre bébé avec élégance.',
      nbArticles: signal(0)
    },
    {
      icon: '🎊',
      titre: 'Cérémonie',
      typeEvenement: 'CEREMONIE',
      description: 'Transformez votre cérémonie en un moment inoubliable avec nos décorations.',
      nbArticles: signal(0)
    },
    {
      icon: '🎂',
      titre: 'Anniversaire',
      typeEvenement: 'ANNIVERSAIRE',
      description: 'Des décorations sur mesure pour fêter vos anniversaires en grande pompe.',
      nbArticles: signal(0)
    },
  ];

  constructor(private catalogueApiService: CatalogueApiService) {}

  ngOnInit() {
    this.chargerNbArticles();
  }

  chargerNbArticles() {
    // Charge le nombre d'articles disponibles pour chaque type d'événement
    this.catalogueApiService.getArticles().subscribe({
      next: (articles) => {
        this.services.forEach(service => {
          const nb = articles.filter(a =>
            a.typeEvenement === service.typeEvenement || a.typeEvenement === 'TOUS'
          ).length;
          service.nbArticles.set(nb);
        });
      },
      error: (err) => console.error('Erreur chargement articles', err)
    });
  }
}
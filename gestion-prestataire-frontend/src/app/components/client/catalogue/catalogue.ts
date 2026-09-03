import { Component, signal, computed, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { TagModule } from 'primeng/tag';
import { SelectModule } from 'primeng/select';
import { FormsModule } from '@angular/forms';
import { PanierApiService, PanierResponse } from '../../../services/api/panier-api';
import { AuthService } from '../../../services/auth';

@Component({
  selector: 'app-catalogue',
  standalone: true,
  imports: [CommonModule, RouterModule, ButtonModule, TagModule, SelectModule, FormsModule],
  templateUrl: './catalogue.html',
  styleUrl: './catalogue.css'
})
export class CatalogueComponent implements OnInit {

  filtreActif = signal<string>('Tous');

  filtreOptions = [
    { label: 'Tous', value: 'Tous' },
    { label: 'Mariage', value: 'Mariage' },
    { label: 'Baptême', value: 'Baptême' },
    { label: 'Cérémonie', value: 'Cérémonie' },
    { label: 'Anniversaire', value: 'Anniversaire' },
  ];

  articles = signal([
    { idArticle: 1, nom: 'Chaise dorée', description: 'Chaise dorée style royal', prixUnitaire: 2500, typeEvenement: 'Mariage', emoji: '🪑' },
    { idArticle: 2, nom: 'Table ronde', description: 'Table ronde 8 personnes', prixUnitaire: 15000, typeEvenement: 'Tous', emoji: '🍽️' },
    { idArticle: 3, nom: 'Couvert complet', description: 'Set assiette verre couverts', prixUnitaire: 3000, typeEvenement: 'Tous', emoji: '🥂' },
    { idArticle: 4, nom: 'Bouquet floral', description: 'Bouquet décoration table', prixUnitaire: 8000, typeEvenement: 'Mariage', emoji: '💐' },
    { idArticle: 5, nom: 'Arche florale', description: 'Arche fleurs naturelles', prixUnitaire: 45000, typeEvenement: 'Mariage', emoji: '🌸' },
    { idArticle: 6, nom: 'Nappe brodée', description: 'Nappe blanche brodée', prixUnitaire: 5000, typeEvenement: 'Tous', emoji: '🎀' },
    { idArticle: 7, nom: 'Ballon décoratif', description: 'Pack 50 ballons colorés', prixUnitaire: 7000, typeEvenement: 'Baptême', emoji: '🎈' },
    { idArticle: 8, nom: 'Sono événementielle', description: 'Système son complet', prixUnitaire: 80000, typeEvenement: 'Tous', emoji: '🎵' },
    { idArticle: 9, nom: 'Photobooth', description: 'Espace photo décoré', prixUnitaire: 35000, typeEvenement: 'Anniversaire', emoji: '📸' },
    { idArticle: 10, nom: 'Pièce montée', description: 'Décoration pièce montée', prixUnitaire: 25000, typeEvenement: 'Mariage', emoji: '🎂' },
  ]);

  // Contient la réponse du panier retournée par Spring Boot
  panier = signal<PanierResponse | null>(null);

  articlesFiltres = computed(() => {
    const filtre = this.filtreActif();
    if (filtre === 'Tous') return this.articles();
    return this.articles().filter(a => a.typeEvenement === filtre || a.typeEvenement === 'Tous');
  });

  // Calculs réactifs basés sur la réponse de l'API
  lignesPanier = computed(() => this.panier()?.lignes ?? []);
  totalPanier  = computed(() => this.panier()?.total ?? 0);
  nombreArticles = computed(() => this.lignesPanier().reduce((acc, l) => acc + l.quantite, 0));

  constructor(
    private panierApiService: PanierApiService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit() {
    this.chargerPanierData();
  }

  // Charge le panier depuis la BDD au chargement de la page
  chargerPanierData() {
    const user = this.authService.getUtilisateur();
    if (!user) return;

    this.panierApiService.getPanier(user.id).subscribe({
      next: (panierData) => this.panier.set(panierData),
      error: (err) => console.error('Erreur chargement panier', err)
    });
  }

  // Enregistre l'article en Base de Données via l'API
  ajouterAuPanier(article: any) {
    const user = this.authService.getUtilisateur();

    console.log('--- DEBUT AJOUT PANIER ---');
    console.log('User connecté :', user);
    console.log('Article sélectionné :', article);

    if (!user || !user.id) {
      console.error('Utilisateur non identifié. Redirection vers login.');
      this.router.navigate(['/auth/login']);
      return;
    }

    const payload = {
      articleId: article.idArticle,
      quantite: 1
    };

    console.log(`Envoi POST /api/client/panier/${user.id}/ajouter avec :`, payload);

    this.panierApiService.ajouterArticle(user.id, payload).subscribe({
      next: (panierMisAJour) => {
        console.log('SUCCÈS Backend - Panier retourné :', panierMisAJour);
        this.panier.set(panierMisAJour);
      },
      error: (err) => {
        console.error('ÉCHEC Backend - Détail de l\'erreur HTTP :', err);
      }
    });
  }

  // Vérifie la présence de l'article dans les lignes du panier retournées par le backend
  estDansPanier(idArticle: number): boolean {
    return this.lignesPanier().some(l => l.articleId === idArticle);
  }

  getCouleurType(type: string) {
    switch (type) {
      case 'Mariage': return 'contrast';
      case 'Baptême': return 'info';
      case 'Cérémonie': return 'success';
      case 'Anniversaire': return 'warn';
      default: return 'secondary';
    }
  }
}

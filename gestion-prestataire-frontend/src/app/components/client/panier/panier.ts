import { Component, signal, computed, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { InputNumberModule } from 'primeng/inputnumber';
import { FormsModule } from '@angular/forms';
import { PanierApiService, PanierResponse } from '../../../services/api/panier-api';
import { AuthService } from '../../../services/auth';

@Component({
  selector: 'app-panier',
  standalone: true,
  imports: [CommonModule, RouterModule, ButtonModule, InputNumberModule, FormsModule],
  templateUrl: './panier.html',
  styleUrl: './panier.css'
})
export class PanierComponent implements OnInit {

  panier     = signal<PanierResponse | null>(null);
  chargement = signal(false);
  erreur     = signal<string | null>(null);   // ← nouveau : afficher l'erreur dans le template

  total  = computed(() => this.panier()?.total ?? 0);
  lignes = computed(() => this.panier()?.lignes ?? []);
  panierVide = computed(() => this.lignes().length === 0);  // ← utile dans le template

  constructor(
    private panierApiService: PanierApiService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit() {
    const user = this.authService.getUtilisateur();
    if (!user) {
      this.router.navigate(['/auth/login']);
      return;
    }
    this.chargerPanier();
  }

  chargerPanier() {
    const user = this.authService.getUtilisateur();
    if (!user) return;

    this.chargement.set(true);
    this.erreur.set(null);

    this.panierApiService.getPanier(user.id).subscribe({
      next: (panier) => {
        this.panier.set(panier);
        this.chargement.set(false);
      },
      error: (err) => {
        this.erreur.set('Impossible de charger le panier');
        this.chargement.set(false);
        console.error('Erreur chargement panier', err);
      }
    });
  }

  modifierQuantite(ligneId: number, quantite: number) {
    const user = this.authService.getUtilisateur();
    if (!user) return;

    if (quantite <= 0) {
      this.supprimerArticle(ligneId);   // ← si quantité = 0, on supprime
      return;
    }

    this.panierApiService.modifierQuantite(user.id, ligneId, quantite).subscribe({
      next: (panier) => this.panier.set(panier),
      error: (err) => console.error('Erreur modification quantité', err)
    });
  }




  supprimerArticle(ligneId: number) {
    const user = this.authService.getUtilisateur();
    if (!user) return;

    this.panierApiService.supprimerArticle(user.id, ligneId).subscribe({
      next: (panier) => this.panier.set(panier),
      error: (err) => console.error('Erreur suppression article', err)
    });
  }
  ajouterArticle(articleId: number) {
    const user = this.authService.getUtilisateur();
    if (!user) return;

    this.chargement.set(true);

    this.panierApiService.ajouterArticle(user.id, { articleId: articleId, quantite: 1 }).subscribe({
      next: (panierMiseAJour) => {
        this.panier.set(panierMiseAJour); // ← Met à jour l'affichage de votre template HTML
        this.chargement.set(false);
        console.log('Article ajouté avec succès !', panierMiseAJour);
      },
      error: (err) => {
        this.erreur.set("Erreur lors de l'ajout de l'article");
        this.chargement.set(false);
        console.error("Erreur lors de l'ajout", err);
      }
    });
  }

  viderPanier() {
    const user = this.authService.getUtilisateur();
    if (!user) return;

    this.panierApiService.viderPanier(user.id).subscribe({
      next: () => this.panier.set(null),
      error: (err) => console.error('Erreur vidage panier', err)
    });
  }

  allerAuPaiement() {
    this.router.navigate(['/client/paiement']);
  }

}

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
  erreur     = signal<string | null>(null);

  // Liste des lignes de panier
  lignes = computed(() => this.panier()?.lignes ?? []);
  panierVide = computed(() => this.lignes().length === 0);

  // Calcul dynamique du total (prend en compte montantTotal/total du backend ou recalcule si 0)
  total = computed(() => {
    const p = this.panier() as any;
    if (!p) return 0;

    const backendTotal = p.montantTotal ?? p.total;
    if (backendTotal && backendTotal > 0) {
      return backendTotal;
    }

    // Recalcul de secours à partir des sous-totaux des lignes
    return this.lignes().reduce((acc, ligne: any) => {
      const sousTotalLigne = ligne.sousTotal ?? (ligne.prixUnitaire * ligne.quantite);
      return acc + (sousTotalLigne || 0);
    }, 0);
  });

  constructor(
    private panierApiService: PanierApiService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    const user = this.authService.getUtilisateur();
    if (!user) {
      this.router.navigate(['/auth/login']);
      return;
    }
    this.chargerPanier();
  }

  chargerPanier(): void {
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

  modifierQuantite(ligneId: number, quantite: number): void {
    const user = this.authService.getUtilisateur();
    if (!user) return;

    if (quantite <= 0) {
      this.supprimerArticle(ligneId);
      return;
    }

    this.panierApiService.modifierQuantite(user.id, ligneId, quantite).subscribe({
      next: (panier) => this.panier.set(panier),
      error: (err) => console.error('Erreur modification quantité', err)
    });
  }

  supprimerArticle(ligneId: number): void {
    const user = this.authService.getUtilisateur();
    if (!user) return;

    this.panierApiService.supprimerArticle(user.id, ligneId).subscribe({
      next: (panier) => this.panier.set(panier),
      error: (err) => console.error('Erreur suppression article', err)
    });
  }

  ajouterArticle(articleId: number): void {
    const user = this.authService.getUtilisateur();
    if (!user) return;

    this.chargement.set(true);

    this.panierApiService.ajouterArticle(user.id, { articleId: articleId, quantite: 1 }).subscribe({
      next: (panierMiseAJour) => {
        this.panier.set(panierMiseAJour);
        this.chargement.set(false);
      },
      error: (err) => {
        this.erreur.set("Erreur lors de l'ajout de l'article");
        this.chargement.set(false);
        console.error("Erreur lors de l'ajout", err);
      }
    });
  }

  viderPanier(): void {
    const user = this.authService.getUtilisateur();
    if (!user) return;

    this.panierApiService.viderPanier(user.id).subscribe({
      next: () => this.panier.set(null),
      error: (err) => console.error('Erreur vidage panier', err)
    });
  }

  allerAuPaiement(): void {
    this.router.navigate(['/client/paiement']);
  }
}

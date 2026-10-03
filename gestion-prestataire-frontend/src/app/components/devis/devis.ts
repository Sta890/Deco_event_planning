import { Component, signal, computed, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { TagModule } from 'primeng/tag';
import { InputNumberModule } from 'primeng/inputnumber';
import { SelectModule } from 'primeng/select';
import { DividerModule } from 'primeng/divider';
import { Router } from '@angular/router';
import { DevisApiService, DevisResponse } from '../../services/api/devis-api';
import { ArticleApiService, ArticleResponse } from '../../services/api/article-api';
import { PrestationApiService, PrestationResponse } from '../../services/api/prestation-api';
import { FactureApiService } from '../../services/api/facture-api';
import { EnumLabelPipe, enumLabel } from '../../shared/pipes/enum-label.pipe';

interface LigneEnCours {
  articleId: number;
  nom: string;
  quantite: number;
  prixUnitaire: number;
  sousTotal: number;
}

@Component({
  selector: 'app-devis',
  standalone: true,
  imports: [
    CommonModule, FormsModule, TableModule, ButtonModule,
    DialogModule, TagModule, InputNumberModule, SelectModule, DividerModule, EnumLabelPipe
  ],
  templateUrl: './devis.html',
  styleUrl: './devis.css'
})
export class DevisComponent implements OnInit {

  devis = signal<DevisResponse[]>([]);
  articlesDisponibles = signal<ArticleResponse[]>([]);
  prestations = signal<PrestationResponse[]>([]);

  prestationOptions = computed(() =>
    this.prestations().map(p => ({ label: `#${p.id} — ${enumLabel(p.typeEvenement)} (${p.clientNom})`, value: p.id }))
  );

  dialogVisible = signal(false);
  prestationSelectionneeId = signal<number | null>(null);
  lignesEnCours = signal<LigneEnCours[]>([]);
  devisEnEdition = signal<DevisResponse | null>(null);

  montantTotal = computed(() =>
    this.lignesEnCours().reduce((total, ligne) => total + ligne.sousTotal, 0)
  );

  constructor(
    private devisApiService: DevisApiService,
    private articleApiService: ArticleApiService,
    private prestationApiService: PrestationApiService,
    private factureApiService: FactureApiService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.chargerDevis();
    this.chargerArticles();
    this.chargerPrestations();
  }

  chargerDevis() {
    this.devisApiService.findAll().subscribe({
      next: (data) => this.devis.set(data),
      error: (err) => console.error('Erreur chargement devis', err)
    });
  }

  chargerArticles() {
    this.articleApiService.findAll().subscribe({
      next: (data) => this.articlesDisponibles.set(data),
      error: (err) => console.error('Erreur chargement articles', err)
    });
  }

  chargerPrestations() {
    this.prestationApiService.findAll().subscribe({
      next: (data) => this.prestations.set(data),
      error: (err) => console.error('Erreur chargement prestations', err)
    });
  }

  ouvrirDialog() {
    this.devisEnEdition.set(null);
    this.prestationSelectionneeId.set(null);
    this.lignesEnCours.set([]);
    this.dialogVisible.set(true);
  }

  ouvrirDialogCompletion(devis: DevisResponse) {
    this.devisEnEdition.set(devis);
    this.prestationSelectionneeId.set(devis.prestationId);
    this.lignesEnCours.set(
      (devis.lignes ?? []).map(l => ({
        articleId: l.articleId,
        nom: l.articleNom,
        quantite: l.quantite,
        prixUnitaire: l.prixUnitaire,
        sousTotal: l.sousTotal,
      }))
    );
    this.dialogVisible.set(true);
  }

  ajouterArticle(article: ArticleResponse) {
    const existante = this.lignesEnCours().find(l => l.articleId === article.id);
    if (existante) {
      this.modifierQuantite(article.id, existante.quantite + 1);
      return;
    }
    this.lignesEnCours.update(list => [...list, {
      articleId: article.id,
      nom: article.nom,
      quantite: 1,
      prixUnitaire: article.prixUnitaire,
      sousTotal: article.prixUnitaire
    }]);
  }

  modifierQuantite(articleId: number, quantite: number) {
    if (quantite <= 0) {
      this.supprimerLigne(articleId);
      return;
    }
    this.lignesEnCours.update(list =>
      list.map(l => l.articleId === articleId
        ? { ...l, quantite, sousTotal: quantite * l.prixUnitaire }
        : l
      )
    );
  }

  supprimerLigne(articleId: number) {
    this.lignesEnCours.update(list => list.filter(l => l.articleId !== articleId));
  }

  sauvegarder() {
    const prestationId = this.prestationSelectionneeId();
    if (!prestationId) {
      alert('Veuillez sélectionner une prestation');
      return;
    }
    if (this.lignesEnCours().length === 0) {
      alert('Ajoutez au moins un article');
      return;
    }

    const devisEnCours = this.devisEnEdition();
    if (devisEnCours) {
      this.devisApiService.mettreAJourLignes(
        devisEnCours.id,
        this.lignesEnCours().map(l => ({ articleId: l.articleId, quantite: l.quantite }))
      ).subscribe({
        next: () => {
          this.dialogVisible.set(false);
          this.devisEnEdition.set(null);
          this.chargerDevis();
        },
        error: (err) => console.error('Erreur mise à jour des lignes', err)
      });
      return;
    }

    this.devisApiService.create({
      prestationId,
      lignes: this.lignesEnCours().map(l => ({ articleId: l.articleId, quantite: l.quantite }))
    }).subscribe({
      next: () => {
        this.dialogVisible.set(false);
        this.chargerDevis();
      },
      error: (err) => console.error('Erreur création devis', err)
    });
  }

  changerStatut(id: number, statut: string) {
    this.devisApiService.updateStatut(id, statut).subscribe({
      next: () => this.chargerDevis(),
      error: (err) => console.error('Erreur mise à jour statut devis', err)
    });
  }

  genererFacture(devis: DevisResponse) {
    this.factureApiService.genererDepuisDevis(devis.id).subscribe({
      next: () => this.router.navigate(['/prestataire/factures']),
      error: (err) => {
        console.error('Erreur génération facture', err);
        alert(err?.error?.message ?? 'Impossible de générer la facture');
      }
    });
  }

  supprimerDevis(id: number) {
    this.devisApiService.delete(id).subscribe({
      next: () => this.chargerDevis(),
      error: (err) => console.error('Erreur suppression devis', err)
    });
  }

  getCouleurStatut(statut: string) {
    switch (statut) {
      case 'VALIDE': return 'success';
      case 'EN_ATTENTE': return 'warn';
      case 'REFUSE': return 'danger';
      default: return 'secondary';
    }
  }
}

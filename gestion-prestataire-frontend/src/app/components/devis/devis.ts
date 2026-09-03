import { Component, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { TagModule } from 'primeng/tag';
import { DatePickerModule } from 'primeng/datepicker';
import { InputNumberModule } from 'primeng/inputnumber';
import { SelectModule } from 'primeng/select';
import { DividerModule } from 'primeng/divider';
import { Devis } from '../../models/devis.model';
import { Article } from '../../models/article.model';
import { LigneDevis } from '../../models/ligne-devis.model';
import { Router } from '@angular/router';
import { Facture } from '../../models/facture.model';

@Component({
  selector: 'app-devis',
  standalone: true,
  imports: [
    CommonModule, FormsModule, TableModule, ButtonModule,
    DialogModule, TagModule, DatePickerModule, InputNumberModule, SelectModule, DividerModule
  ],
  templateUrl: './devis.html',
  styleUrl: './devis.css'
})
export class DevisComponent {

  // Catalogue articles disponibles
  articlesDisponibles = signal<Article[]>([
    { idArticle: 1, nom: 'Chaise dorée', description: 'Chaise dorée style royal', prixUnitaire: 2500, typeEvenement: 'Mariage' },
    { idArticle: 2, nom: 'Table ronde', description: 'Table ronde 8 personnes', prixUnitaire: 15000, typeEvenement: 'Tous' },
    { idArticle: 3, nom: 'Couvert complet', description: 'Set assiette verre couverts', prixUnitaire: 3000, typeEvenement: 'Tous' },
    { idArticle: 4, nom: 'Bouquet floral', description: 'Bouquet décoration table', prixUnitaire: 8000, typeEvenement: 'Mariage' },
    { idArticle: 5, nom: 'Arche florale', description: 'Arche fleurs naturelles', prixUnitaire: 45000, typeEvenement: 'Mariage' },
    { idArticle: 6, nom: 'Nappe brodée', description: 'Nappe blanche brodée', prixUnitaire: 5000, typeEvenement: 'Tous' },
    { idArticle: 7, nom: 'Ballon décoratif', description: 'Pack 50 ballons colorés', prixUnitaire: 7000, typeEvenement: 'Baptême' },
    { idArticle: 8, nom: 'Sono événementielle', description: 'Système son complet', prixUnitaire: 80000, typeEvenement: 'Tous' },
  ]);

  prestationOptions = signal([
    { label: 'Mariage Famille Dupont', value: 1 },
    { label: 'Baptême Bébé Sarah', value: 2 },
    { label: 'Cérémonie Hôtel Renaissance', value: 3 },
  ]);

  clientOptions = signal([
    { label: 'Hôtel Renaissance', value: 1 },
    { label: 'Mme. Sarah', value: 2 },
    { label: 'Boutique Éclat', value: 3 },
  ]);

  devis = signal<Devis[]>([
    {
      idDevis: 1,
      dateCreation: new Date('2026-01-10'),
      statut: 'Validé',
      idPrestation: 1,
      idClient: 1,
      lignes: [
        { idArticle: 1, nom: 'Chaise dorée', quantite: 50, prixUnitaire: 2500, sousTotal: 125000 },
        { idArticle: 5, nom: 'Arche florale', quantite: 1, prixUnitaire: 45000, sousTotal: 45000 },
      ],
      montantTotal: 170000
    },
  ]);

  dialogVisible = signal(false);
  lignesEnCours = signal<LigneDevis[]>([]);

  statutOptions = [
    { label: 'En attente', value: 'En attente' },
    { label: 'Validé', value: 'Validé' },
    { label: 'Refusé', value: 'Refusé' },
  ];

  devisSelectionne = signal<Devis>({
    idDevis: 0, dateCreation: new Date(), statut: 'En attente',
    idPrestation: 0, idClient: 0, lignes: [], montantTotal: 0
  });

  montantTotal = computed(() =>
    this.lignesEnCours().reduce((total, ligne) => total + ligne.sousTotal, 0)
  );

  ouvrirDialog() {
    this.devisSelectionne.set({
      idDevis: 0, dateCreation: new Date(), statut: 'En attente',
      idPrestation: 0, idClient: 0, lignes: [], montantTotal: 0
    });
    this.lignesEnCours.set([]);
    this.dialogVisible.set(true);
  }

  modifierDevis(devis: Devis) {
    this.devisSelectionne.set({ ...devis });
    this.lignesEnCours.set([...devis.lignes]);
    this.dialogVisible.set(true);
  }

  supprimerDevis(id: number) {
    this.devis.update((list: Devis[]) => list.filter(d => d.idDevis !== id));
  }

  mettreAJourChamp(champ: keyof Devis, valeur: any) {
    this.devisSelectionne.update(d => ({ ...d, [champ]: valeur }));
  }

  ajouterArticle(article: Article) {
    const lignes = this.lignesEnCours();
    const existant = lignes.find(l => l.idArticle === article.idArticle);
    if (existant) {
      this.lignesEnCours.update(list =>
        list.map(l => l.idArticle === article.idArticle
          ? { ...l, quantite: l.quantite + 1, sousTotal: (l.quantite + 1) * l.prixUnitaire }
          : l
        )
      );
    } else {
      this.lignesEnCours.update(list => [...list, {
        idArticle: article.idArticle,
        nom: article.nom,
        quantite: 1,
        prixUnitaire: article.prixUnitaire,
        sousTotal: article.prixUnitaire
      }]);
    }
  }

  modifierQuantite(idArticle: number, quantite: number) {
    if (quantite <= 0) {
      this.supprimerLigne(idArticle);
      return;
    }
    this.lignesEnCours.update(list =>
      list.map(l => l.idArticle === idArticle
        ? { ...l, quantite, sousTotal: quantite * l.prixUnitaire }
        : l
      )
    );
  }

  supprimerLigne(idArticle: number) {
    this.lignesEnCours.update(list => list.filter(l => l.idArticle !== idArticle));
  }

  sauvegarder() {
    const d = this.devisSelectionne();
    const devisComplet: Devis = {
      ...d,
      lignes: this.lignesEnCours(),
      montantTotal: this.montantTotal()
    };
    if (d.idDevis === 0) {
      this.devis.update((list: Devis[]) => [...list, { ...devisComplet, idDevis: list.length + 1 }]);
    } else {
      this.devis.update((list: Devis[]) => list.map(x => x.idDevis === d.idDevis ? devisComplet : x));
    }
    this.dialogVisible.set(false);
  }

  getCouleurStatut(statut: string) {
    switch (statut) {
      case 'Validé': return 'success';
      case 'En attente': return 'warn';
      case 'Refusé': return 'danger';
      default: return 'secondary';
    }
  }
  constructor(private router: Router) {}

  factures = signal<Facture[]>([]);

genererFacture(devis: Devis) {
  // Vérifie si une facture existe déjà pour ce devis
  const factureExistante = this.factures().find(f => f.idDevis === devis.idDevis);
  if (factureExistante) {
    alert('Une facture existe déjà pour ce devis !');
    return;
  }

  // Crée la facture automatiquement
  const nouvelleFacture: Facture = {
    idFacture: this.factures().length + 1,
    dateFacture: new Date(),
    montantTotal: devis.montantTotal,
    statut: 'En attente',
    idDevis: devis.idDevis
  };

  this.factures.update((list: Facture[]) => [...list, nouvelleFacture]);

  // Redirige vers la page Factures
  this.router.navigate(['/prestataire/factures']);
}
}
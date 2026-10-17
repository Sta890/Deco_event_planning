import { Component, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { SelectModule } from 'primeng/select';
import { DatePickerModule } from 'primeng/datepicker';
import { RouterModule } from '@angular/router';
import { DemandeApiService } from '../../../services/api/demande-api';
import { PanierApiService } from '../../../services/api/panier-api';
import { AuthService } from '../../../services/auth';

@Component({
  selector: 'app-demande-devis',
  standalone: true,
  imports: [CommonModule, FormsModule, ButtonModule, InputTextModule, SelectModule, DatePickerModule, RouterModule],
  templateUrl: './demande-devis.html',
  styleUrl: './demande-devis.css'
})
export class DemandeDevisComponent {

  envoye = signal(false);
  erreur = signal('');
  chargement = signal(false);

  /** Articles du panier, chiffrés et envoyés avec la demande pour que le devis ne soit pas à 0. */
  lignesPanier = signal<{ articleId: number; quantite: number; nom: string; sousTotal: number }[]>([]);
  totalPanier = computed(() => this.lignesPanier().reduce((total, ligne) => total + ligne.sousTotal, 0));

  typeEvenementOptions = [
    { label: 'Mariage', value: 'MARIAGE' },
    { label: 'Baptême', value: 'BAPTEME' },
    { label: 'Cérémonie', value: 'CEREMONIE' },
    { label: 'Anniversaire', value: 'ANNIVERSAIRE' },
    { label: 'Autre', value: 'AUTRE' },
  ];

  formulaire = signal({
    nom: '',
    telephone: '',
    email: '',
    typeEvenement: '',
    dateEvenement: null as Date | null,
    lieu: '',
    nombrePersonnes: '',
    message: '',
  });

  constructor(
    private demandeApiService: DemandeApiService,
    private panierApiService: PanierApiService,
    private authService: AuthService
  ) {
    const utilisateur = this.authService.getUtilisateur();
    if (utilisateur) {
      this.formulaire.update(f => ({
        ...f,
        nom: f.nom || utilisateur.nom,
        email: f.email || utilisateur.email
      }));
      this.chargerPanier(utilisateur.id);
    }
  }

  chargerPanier(utilisateurId: number): void {
    this.panierApiService.getPanier(utilisateurId).subscribe({
      next: (panier) => this.lignesPanier.set(
        (panier.lignes ?? []).map(l => ({
          articleId: l.articleId,
          quantite: l.quantite,
          nom: l.articleNom,
          sousTotal: l.sousTotal
        }))
      ),
      error: (err) => console.error('Erreur chargement panier', err)
    });
  }

  viderPanier(): void {
    const utilisateur = this.authService.getUtilisateur();
    if (!utilisateur) return;
    this.panierApiService.viderPanier(utilisateur.id).subscribe({
      error: (err) => console.error('Erreur vidage panier', err)
    });
  }

  mettreAJour(champ: string, valeur: any) {
    this.formulaire.update(f => ({ ...f, [champ]: valeur }));
  }

  envoyer() {
    const f = this.formulaire();
    if (!f.nom || !f.telephone || !f.typeEvenement || !f.dateEvenement) {
      this.erreur.set('Veuillez remplir tous les champs obligatoires !');
      return;
    }

    this.erreur.set('');
    this.chargement.set(true);

    this.demandeApiService.envoyer({
      nom: f.nom,
      telephone: f.telephone,
      email: f.email,
      typeEvenement: f.typeEvenement,
      dateEvenement: this.toIsoDate(f.dateEvenement),
      lieu: f.lieu,
      nombrePersonnes: f.nombrePersonnes ? Number(f.nombrePersonnes) : null,
      message: f.message,
      lignes: this.lignesPanier().map(l => ({ articleId: l.articleId, quantite: l.quantite }))
    }).subscribe({
      next: () => {
        this.chargement.set(false);
        this.envoye.set(true);
        this.viderPanier();
      },
      error: (err) => {
        this.chargement.set(false);
        this.erreur.set('Erreur lors de l\'envoi de la demande. Réessayez !');
        console.error('Erreur demande de devis', err);
      }
    });
  }

  private toIsoDate(date: Date): string {
    const mois = `${date.getUTCMonth() + 1}`.padStart(2, '0');
    const jour = `${date.getUTCDate()}`.padStart(2, '0');
    return `${date.getUTCFullYear()}-${mois}-${jour}`;
  }
}

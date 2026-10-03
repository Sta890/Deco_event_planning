import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { SelectModule } from 'primeng/select';
import { DatePickerModule } from 'primeng/datepicker';
import { RouterModule } from '@angular/router';
import { DemandeApiService } from '../../../services/api/demande-api';
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
    private authService: AuthService
  ) {
    const utilisateur = this.authService.getUtilisateur();
    if (utilisateur) {
      this.formulaire.update(f => ({
        ...f,
        nom: f.nom || utilisateur.nom,
        email: f.email || utilisateur.email
      }));
    }
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
      message: f.message
    }).subscribe({
      next: () => {
        this.chargement.set(false);
        this.envoye.set(true);
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

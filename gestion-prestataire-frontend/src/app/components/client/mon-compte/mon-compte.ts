import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { TagModule } from 'primeng/tag';
import { AuthService } from '../../../services/auth';

@Component({
  selector: 'app-mon-compte',
  standalone: true,
  imports: [CommonModule, RouterModule, TagModule],
  templateUrl: './mon-compte.html',
  styleUrl: './mon-compte.css'
})
export class MonCompteComponent {

  ongletActif = signal<'devis' | 'factures'>('devis');

  mesDevis = signal([
    { idDevis: 1, typeEvenement: 'Mariage', dateCreation: new Date('2026-01-10'), montantTotal: 170000, statut: 'Validé' },
    { idDevis: 2, typeEvenement: 'Baptême', dateCreation: new Date('2026-02-15'), montantTotal: 85000, statut: 'En attente' },
  ]);

  mesFactures = signal([
    { idFacture: 1, typeEvenement: 'Mariage', dateFacture: new Date('2026-01-15'), montantTotal: 170000, statut: 'Payé' },
    { idFacture: 2, typeEvenement: 'Baptême', dateFacture: new Date('2026-02-20'), montantTotal: 85000, statut: 'En attente' },
  ]);

  constructor(private authService: AuthService) {}

  getEmail() {
    return this.authService.getUtilisateur()?.email ?? '';
  }

  getCouleurStatut(statut: string) {
    switch (statut) {
      case 'Validé': case 'Payé': return 'success';
      case 'En attente': return 'warn';
      case 'Refusé': case 'En retard': return 'danger';
      default: return 'secondary';
    }
  }
}
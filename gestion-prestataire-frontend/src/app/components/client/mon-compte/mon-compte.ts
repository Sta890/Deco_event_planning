import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { TagModule } from 'primeng/tag';
import { AuthService } from '../../../services/auth';
import { DevisApiService } from '../../../services/api/devis-api';
import { FactureApiService } from '../../../services/api/facture-api';

@Component({
  selector: 'app-mon-compte',
  standalone: true,
  imports: [CommonModule, RouterModule, TagModule],
  templateUrl: './mon-compte.html',
  styleUrl: './mon-compte.css'
})
export class MonCompteComponent implements OnInit {

  ongletActif = signal<'devis' | 'factures'>('devis');

  mesDevis = signal<any[]>([]);
  mesFactures = signal<any[]>([]);

  constructor(
    private authService: AuthService,
    private devisApiService: DevisApiService,
    private factureApiService: FactureApiService
  ) {}

  ngOnInit(): void {
    const user = this.authService.getUtilisateur();
    if (user && user.id) {
      this.chargerDonnees(user.id);
    }
  }

  chargerDonnees(clientId: number): void {
    // Appelle la méthode GET client de devis-api.ts
    this.devisApiService.findByClientId(clientId).subscribe({
      next: (data) => this.mesDevis.set(data),
      error: (err) => console.error('Erreur devis', err)
    });

    // Appelle la méthode GET client de facture-api.ts
    this.factureApiService.findByClientId(clientId).subscribe({
      next: (data) => this.mesFactures.set(data),
      error: (err) => console.error('Erreur factures', err)
    });
  }

  getEmail() {
    return this.authService.getUtilisateur()?.email ?? '';
  }

  getCouleurStatut(statut: string) {
    switch (statut?.toUpperCase()) {
      case 'VALIDE': case 'PAYE': case 'VALIDÉ': case 'PAYÉ': return 'success';
      case 'EN_ATTENTE': return 'warn';
      case 'REFUSE': case 'ANNULE': return 'danger';
      default: return 'secondary';
    }
  }
}

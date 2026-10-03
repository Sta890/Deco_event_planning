import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { TagModule } from 'primeng/tag';
import { AuthService } from '../../../services/auth';
import { DevisApiService, DevisResponse } from '../../../services/api/devis-api';
import { FactureApiService, FactureResponse } from '../../../services/api/facture-api';
import { EnumLabelPipe } from '../../../shared/pipes/enum-label.pipe';

@Component({
  selector: 'app-mon-compte',
  standalone: true,
  imports: [CommonModule, RouterModule, TagModule, EnumLabelPipe],
  templateUrl: './mon-compte.html',
  styleUrl: './mon-compte.css'
})
export class MonCompteComponent implements OnInit {

  ongletActif = signal<'devis' | 'factures'>('devis');

  mesDevis = signal<DevisResponse[]>([]);
  mesFactures = signal<FactureResponse[]>([]);

  constructor(
    private authService: AuthService,
    private devisApiService: DevisApiService,
    private factureApiService: FactureApiService
  ) {}

  ngOnInit(): void {
    this.chargerDonnees();
  }

  chargerDonnees(): void {
    this.devisApiService.getMesDevis().subscribe({
      next: (data) => this.mesDevis.set(data),
      error: (err) => console.error('Erreur devis', err)
    });

    this.factureApiService.getMesFactures().subscribe({
      next: (data) => this.mesFactures.set(data),
      error: (err) => console.error('Erreur factures', err)
    });
  }

  getEmail() {
    return this.authService.getUtilisateur()?.email ?? '';
  }

  getCouleurStatut(statut: string) {
    switch (statut?.toUpperCase()) {
      case 'VALIDE': case 'PAYE': return 'success';
      case 'EN_ATTENTE': return 'warn';
      case 'REFUSE': case 'EN_RETARD': return 'danger';
      default: return 'secondary';
    }
  }
}

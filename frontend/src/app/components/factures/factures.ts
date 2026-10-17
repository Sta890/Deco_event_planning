import { Component, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { TagModule } from 'primeng/tag';
import { SelectModule } from 'primeng/select';
import { FactureApiService, FactureResponse } from '../../services/api/facture-api';
import { EnumLabelPipe } from '../../shared/pipes/enum-label.pipe';

@Component({
  selector: 'app-factures',
  standalone: true,
  imports: [
    CommonModule, FormsModule, TableModule, ButtonModule, TagModule, SelectModule, EnumLabelPipe
  ],
  templateUrl: './factures.html',
  styleUrl: './factures.css'
})
export class FacturesComponent implements OnInit {

  factures = signal<FactureResponse[]>([]);

  // Le statut PAYE n'est pas proposé ici : il est positionné automatiquement
  // par le backend lors de l'enregistrement d'un paiement (PaiementService).
  statutOptions = [
    { label: 'En attente', value: 'EN_ATTENTE' },
    { label: 'En retard', value: 'EN_RETARD' },
  ];

  constructor(private factureApiService: FactureApiService) {}

  ngOnInit(): void {
    this.chargerFactures();
  }

  chargerFactures() {
    this.factureApiService.findAll().subscribe({
      next: (data) => this.factures.set(data),
      error: (err) => console.error('Erreur chargement factures', err)
    });
  }

  changerStatut(id: number, statut: string) {
    this.factureApiService.updateStatut(id, statut).subscribe({
      next: () => this.chargerFactures(),
      error: (err) => console.error('Erreur mise à jour statut facture', err)
    });
  }

  supprimerFacture(id: number) {
    this.factureApiService.delete(id).subscribe({
      next: () => this.chargerFactures(),
      error: (err) => console.error('Erreur suppression facture', err)
    });
  }

  getCouleurStatut(statut: string) {
    switch (statut) {
      case 'PAYE': return 'success';
      case 'EN_ATTENTE': return 'warn';
      case 'EN_RETARD': return 'danger';
      default: return 'secondary';
    }
  }
}

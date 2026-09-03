  import { Component, signal } from '@angular/core';
  import { CommonModule } from '@angular/common';
  import { FormsModule } from '@angular/forms';
  import { TableModule } from 'primeng/table';
  import { ButtonModule } from 'primeng/button';
  import { ChipModule } from 'primeng/chip';
  import { DatePickerModule } from 'primeng/datepicker';
  import { IHistorique } from '../../models/historique.model';

  @Component({
    selector: 'app-historique',
    standalone: true,
    imports: [
      CommonModule, FormsModule, TableModule, ButtonModule,
      ChipModule, DatePickerModule
    ],
    templateUrl: './historique.html',
    styleUrl: './historique.css'
  })
  export class HistoriqueComponent {

    historiques = signal<IHistorique[]>([
      { idHistorique: 1, action: 'Création client Hôtel Renaissance', dateAction: new Date('2026-01-10') },
      { idHistorique: 2, action: 'Génération devis #1', dateAction: new Date('2026-01-15') },
      { idHistorique: 3, action: 'Facture envoyée au client Mme. Sarah', dateAction: new Date('2026-02-20') },
      { idHistorique: 4, action: 'Paiement reçu Boutique Éclat', dateAction: new Date('2026-03-25') },
    ]);

    dateFiltre = signal<Date | null>(null);

    historiquesFiltres = () => {
      const date = this.dateFiltre();
      if (!date) return this.historiques();
      return this.historiques().filter(h =>
        new Date(h.dateAction).toDateString() === date.toDateString()
      );
    }

    reinitialiserFiltre() {
      this.dateFiltre.set(null);
    }
  }
import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { TagModule } from 'primeng/tag';
import { DatePickerModule } from 'primeng/datepicker';
import { InputNumberModule } from 'primeng/inputnumber';
import { SelectModule } from 'primeng/select';
import { Paiement } from '../../models/paiement.model';

@Component({
  selector: 'app-paiements',
  standalone: true,
  imports: [
    CommonModule, FormsModule, TableModule, ButtonModule,
    DialogModule, TagModule, DatePickerModule, InputNumberModule, SelectModule
  ],
  templateUrl: './paiements.html',
  styleUrl: './paiements.css'
})
export class PaiementsComponent {

  paiements = signal<Paiement[]>([
    { idPaiement: 1, datePaiement: new Date('2026-01-20'), montant: 3500, modePaiement: 'Virement', idFacture: 1 },
    { idPaiement: 2, datePaiement: new Date('2026-02-25'), montant: 850, modePaiement: 'Mobile Money', idFacture: 2 },
    { idPaiement: 3, datePaiement: new Date('2026-03-30'), montant: 1800, modePaiement: 'Cash', idFacture: 3 },
  ]);

  dialogVisible = signal(false);

  modePaiementOptions = [
    { label: 'Cash', value: 'Cash' },
    { label: 'Mobile Money', value: 'Mobile Money' },
    { label: 'Virement', value: 'Virement' },
  ];

  paiementSelectionne = signal<Paiement>({
    idPaiement: 0, datePaiement: new Date(), montant: 0, modePaiement: 'Cash', idFacture: 0
  });

  ouvrirDialog() {
    this.paiementSelectionne.set({ idPaiement: 0, datePaiement: new Date(), montant: 0, modePaiement: 'Cash', idFacture: 0 });
    this.dialogVisible.set(true);
  }

  modifierPaiement(paiement: Paiement) {
    this.paiementSelectionne.set({ ...paiement });
    this.dialogVisible.set(true);
  }

  supprimerPaiement(id: number) {
    this.paiements.update((list: Paiement[]) => list.filter(p => p.idPaiement !== id));
  }

  mettreAJourChamp(champ: keyof Paiement, valeur: any) {
    this.paiementSelectionne.update(p => ({ ...p, [champ]: valeur }));
  }

  sauvegarder() {
    const p = this.paiementSelectionne();
    if (p.idPaiement === 0) {
      this.paiements.update((list: Paiement[]) => [...list, { ...p, idPaiement: list.length + 1 }]);
    } else {
      this.paiements.update((list: Paiement[]) => list.map(x => x.idPaiement === p.idPaiement ? { ...p } : x));
    }
    this.dialogVisible.set(false);
  }

  getCouleurMode(mode: string) {
    switch (mode) {
      case 'Cash': return 'success';
      case 'Mobile Money': return 'warn';
      case 'Virement': return 'info';
      default: return 'secondary';
    }
  }
}
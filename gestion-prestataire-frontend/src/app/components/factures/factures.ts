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
import { Facture } from '../../models/facture.model';

@Component({
  selector: 'app-factures',
  standalone: true,
  imports: [
    CommonModule, FormsModule, TableModule, ButtonModule,
    DialogModule, TagModule, DatePickerModule, InputNumberModule, SelectModule
  ],
  templateUrl: './factures.html',
  styleUrl: './factures.css'
})
export class FacturesComponent {

  factures = signal<Facture[]>([
    { idFacture: 1, dateFacture: new Date('2026-01-15'), montantTotal: 3500, statut: 'Payé', idDevis: 1 },
    { idFacture: 2, dateFacture: new Date('2026-02-20'), montantTotal: 850, statut: 'En attente', idDevis: 2 },
    { idFacture: 3, dateFacture: new Date('2026-03-25'), montantTotal: 1800, statut: 'En retard', idDevis: 3 },
  ]);

  dialogVisible = signal(false);

  statutOptions = [
    { label: 'Payé', value: 'Payé' },
    { label: 'En attente', value: 'En attente' },
    { label: 'En retard', value: 'En retard' },
  ];

  factureSelectionnee = signal<Facture>({
    idFacture: 0, dateFacture: new Date(), montantTotal: 0, statut: 'En attente', idDevis: 0
  });

  ouvrirDialog() {
    this.factureSelectionnee.set({ idFacture: 0, dateFacture: new Date(), montantTotal: 0, statut: 'En attente', idDevis: 0 });
    this.dialogVisible.set(true);
  }

  modifierFacture(facture: Facture) {
    this.factureSelectionnee.set({ ...facture });
    this.dialogVisible.set(true);
  }

  supprimerFacture(id: number) {
    this.factures.update(list => list.filter(f => f.idFacture !== id));
  }

  mettreAJourChamp(champ: keyof Facture, valeur: any) {
    this.factureSelectionnee.update(f => ({ ...f, [champ]: valeur }));
  }

  sauvegarder() {
    const f = this.factureSelectionnee();
    if (f.idFacture === 0) {
      this.factures.update(list => [...list, { ...f, idFacture: list.length + 1 }]);
    } else {
      this.factures.update(list => list.map(x => x.idFacture === f.idFacture ? { ...f } : x));
    }
    this.dialogVisible.set(false);
  }

  getCouleurStatut(statut: string) {
    switch (statut) {
      case 'Payé': return 'success';
      case 'En attente': return 'warn';
      case 'En retard': return 'danger';
      default: return 'secondary';
    }
  }
}
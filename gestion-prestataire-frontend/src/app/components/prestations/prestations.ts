import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { TagModule } from 'primeng/tag';
import { DatePickerModule } from 'primeng/datepicker';
import { InputTextModule } from 'primeng/inputtext';
import { SelectModule } from 'primeng/select';

import { Prestation } from '../../models/prestation.model';

@Component({
  selector: 'app-prestations',
  standalone: true,
  imports: [
    CommonModule, FormsModule, TableModule, ButtonModule,
    DialogModule, TagModule, DatePickerModule, InputTextModule, SelectModule, 
  ],
  templateUrl: './prestations.html',
  styleUrl: './prestations.css'
})
export class PrestationsComponent {

  prestations = signal<Prestation[]>([
    { idPrestation: 1, typeEvenement: 'Mariage', dateEvenement: new Date('2026-06-15'), lieu: 'Hôtel Renaissance Yaoundé', description: 'Décoration salle et extérieur', idClient: 1 },
    { idPrestation: 2, typeEvenement: 'Baptême', dateEvenement: new Date('2026-07-20'), lieu: 'Église Centrale Douala', description: 'Décoration florale et tables', idClient: 2 },
    { idPrestation: 3, typeEvenement: 'Cérémonie', dateEvenement: new Date('2026-08-10'), lieu: 'Salle des Fêtes Bafoussam', description: 'Décoration complète cérémonie', idClient: 3 },
  ]);

  dialogVisible = signal(false);

  typeEvenementOptions = [
    { label: 'Mariage', value: 'Mariage' },
    { label: 'Baptême', value: 'Baptême' },
    { label: 'Cérémonie', value: 'Cérémonie' },
    { label: 'Anniversaire', value: 'Anniversaire' },
    { label: 'Autre', value: 'Autre' },
  ];

  prestationSelectionnee = signal<Prestation>({
    idPrestation: 0, typeEvenement: 'Mariage', dateEvenement: new Date(), lieu: '', description: '', idClient: 0
  });

  ouvrirDialog() {
    this.prestationSelectionnee.set({ idPrestation: 0, typeEvenement: 'Mariage', dateEvenement: new Date(), lieu: '', description: '', idClient: 0 });
    this.dialogVisible.set(true);
  }

  modifierPrestation(prestation: Prestation) {
    this.prestationSelectionnee.set({ ...prestation });
    this.dialogVisible.set(true);
  }

  supprimerPrestation(id: number) {
    this.prestations.update((list: Prestation[]) => list.filter(p => p.idPrestation !== id));
  }

  mettreAJourChamp(champ: keyof Prestation, valeur: any) {
    this.prestationSelectionnee.update(p => ({ ...p, [champ]: valeur }));
  }

  sauvegarder() {
    const p = this.prestationSelectionnee();
    if (p.idPrestation === 0) {
      this.prestations.update((list: Prestation[]) => [...list, { ...p, idPrestation: list.length + 1 }]);
    } else {
      this.prestations.update((list: Prestation[]) => list.map(x => x.idPrestation === p.idPrestation ? { ...p } : x));
    }
    this.dialogVisible.set(false);
  }

  getCouleurEvenement(type: string) {
    switch (type) {
      case 'Mariage': return 'contrast';
      case 'Baptême': return 'info';
      case 'Cérémonie': return 'success';
      case 'Anniversaire': return 'warn';
      default: return 'secondary';
    }
  }
}
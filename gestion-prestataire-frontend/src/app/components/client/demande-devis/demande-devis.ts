import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { SelectModule } from 'primeng/select';
import { DatePickerModule } from 'primeng/datepicker';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-demande-devis',
  standalone: true,
  imports: [CommonModule, FormsModule, ButtonModule, InputTextModule, SelectModule, DatePickerModule, RouterModule],
  templateUrl: './demande-devis.html',
  styleUrl: './demande-devis.css'
})
export class DemandeDevisComponent {

  envoye = signal(false);

  typeEvenementOptions = [
    { label: 'Mariage', value: 'Mariage' },
    { label: 'Baptême', value: 'Baptême' },
    { label: 'Cérémonie', value: 'Cérémonie' },
    { label: 'Anniversaire', value: 'Anniversaire' },
    { label: 'Autre', value: 'Autre' },
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

  mettreAJour(champ: string, valeur: any) {
    this.formulaire.update(f => ({ ...f, [champ]: valeur }));
  }

  envoyer() {
    const f = this.formulaire();
    if (!f.nom || !f.telephone || !f.typeEvenement || !f.dateEvenement) {
      alert('Veuillez remplir tous les champs obligatoires !');
      return;
    }
    this.envoye.set(true);
  }
}
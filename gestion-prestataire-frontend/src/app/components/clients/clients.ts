import { Component, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { Client } from '../../models/client.model';

@Component({
  selector: 'app-clients',
  standalone: true,
  imports: [CommonModule, FormsModule, TableModule, ButtonModule, DialogModule, InputTextModule],
  templateUrl: './clients.html',
  styleUrl: './clients.css'
})
export class ClientsComponent {

  clients = signal<Client[]>([
    { idClient: 1, nom: 'Hôtel Renaissance', telephone: '655000001', adresse: 'Yaoundé', email: 'hotel@renaissance.cm' },
    { idClient: 2, nom: 'Mme. Sarah', telephone: '699000002', adresse: 'Douala', email: 'sarah@salon.cm' },
    { idClient: 3, nom: 'Boutique Éclat', telephone: '677000003', adresse: 'Bafoussam', email: 'eclat@boutique.cm' },
  ]);

  dialogVisible = signal(false);

  clientSelectionne = signal<Client>({
    idClient: 0, nom: '', telephone: '', adresse: '', email: ''
  });

  ouvrirDialog() {
    this.clientSelectionne.set({ idClient: 0, nom: '', telephone: '', adresse: '', email: '' });
    this.dialogVisible.set(true);
  }

  modifierClient(client: Client) {
    this.clientSelectionne.set({ ...client });
    this.dialogVisible.set(true);
  }

  supprimerClient(id: number) {
    this.clients.update(list => list.filter(c => c.idClient !== id));
  }

  sauvegarder() {
    const c = this.clientSelectionne();
    if (c.idClient === 0) {
      this.clients.update(list => [...list, { ...c, idClient: list.length + 1 }]);
    } else {
      this.clients.update(list => list.map(x => x.idClient === c.idClient ? { ...c } : x));
    }
    this.dialogVisible.set(false);
  }

  mettreAJourChamp(champ: keyof Client, valeur: string) {
    this.clientSelectionne.update(c => ({ ...c, [champ]: valeur }));
  }
}
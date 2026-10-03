import { Component, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { ClientApiService, ClientRequest, ClientResponse } from '../../services/api/client-api';

@Component({
  selector: 'app-clients',
  standalone: true,
  imports: [CommonModule, FormsModule, TableModule, ButtonModule, DialogModule, InputTextModule],
  templateUrl: './clients.html',
  styleUrl: './clients.css'
})
export class ClientsComponent implements OnInit {

  clients = signal<ClientResponse[]>([]);
  dialogVisible = signal(false);

  clientSelectionne = signal<ClientResponse>({
    id: 0, nom: '', telephone: '', adresse: '', email: '', createdAt: ''
  });

  constructor(private clientApiService: ClientApiService) {}

  ngOnInit(): void {
    this.chargerClients();
  }

  chargerClients() {
    this.clientApiService.findAll().subscribe({
      next: (data) => this.clients.set(data),
      error: (err) => console.error('Erreur chargement clients', err)
    });
  }

  ouvrirDialog() {
    this.clientSelectionne.set({ id: 0, nom: '', telephone: '', adresse: '', email: '', createdAt: '' });
    this.dialogVisible.set(true);
  }

  modifierClient(client: ClientResponse) {
    this.clientSelectionne.set({ ...client });
    this.dialogVisible.set(true);
  }

  supprimerClient(id: number) {
    this.clientApiService.delete(id).subscribe({
      next: () => this.chargerClients(),
      error: (err) => console.error('Erreur suppression client', err)
    });
  }

  mettreAJourChamp(champ: keyof ClientResponse, valeur: string) {
    this.clientSelectionne.update(c => ({ ...c, [champ]: valeur }));
  }

  sauvegarder() {
    const c = this.clientSelectionne();
    const request: ClientRequest = {
      nom: c.nom,
      telephone: c.telephone,
      adresse: c.adresse,
      email: c.email
    };

    const requete = c.id === 0
      ? this.clientApiService.create(request)
      : this.clientApiService.update(c.id, request);

    requete.subscribe({
      next: () => {
        this.dialogVisible.set(false);
        this.chargerClients();
      },
      error: (err) => console.error('Erreur sauvegarde client', err)
    });
  }
}

import { Component, signal, computed, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { TagModule } from 'primeng/tag';
import { DatePickerModule } from 'primeng/datepicker';
import { InputTextModule } from 'primeng/inputtext';
import { SelectModule } from 'primeng/select';
import { PrestationApiService, PrestationRequest, PrestationResponse } from '../../services/api/prestation-api';
import { ClientApiService, ClientResponse } from '../../services/api/client-api';
import { EnumLabelPipe } from '../../shared/pipes/enum-label.pipe';

interface PrestationForm {
  id: number;
  typeEvenement: string;
  dateEvenement: Date;
  lieu: string;
  description: string;
  clientId: number | null;
}

@Component({
  selector: 'app-prestations',
  standalone: true,
  imports: [
    CommonModule, FormsModule, TableModule, ButtonModule,
    DialogModule, TagModule, DatePickerModule, InputTextModule, SelectModule, EnumLabelPipe
  ],
  templateUrl: './prestations.html',
  styleUrl: './prestations.css'
})
export class PrestationsComponent implements OnInit {

  prestations = signal<PrestationResponse[]>([]);
  clients = signal<ClientResponse[]>([]);

  clientOptions = computed(() =>
    this.clients().map(c => ({ label: c.nom, value: c.id }))
  );

  dialogVisible = signal(false);

  typeEvenementOptions = [
    { label: 'Mariage', value: 'MARIAGE' },
    { label: 'Baptême', value: 'BAPTEME' },
    { label: 'Cérémonie', value: 'CEREMONIE' },
    { label: 'Anniversaire', value: 'ANNIVERSAIRE' },
    { label: 'Autre', value: 'AUTRE' },
  ];

  prestationSelectionnee = signal<PrestationForm>({
    id: 0, typeEvenement: 'MARIAGE', dateEvenement: new Date(), lieu: '', description: '', clientId: null
  });

  constructor(
    private prestationApiService: PrestationApiService,
    private clientApiService: ClientApiService
  ) {}

  ngOnInit(): void {
    this.chargerPrestations();
    this.chargerClients();
  }

  chargerPrestations() {
    this.prestationApiService.findAll().subscribe({
      next: (data) => this.prestations.set(data),
      error: (err) => console.error('Erreur chargement prestations', err)
    });
  }

  chargerClients() {
    this.clientApiService.findAll().subscribe({
      next: (data) => this.clients.set(data),
      error: (err) => console.error('Erreur chargement clients', err)
    });
  }

  ouvrirDialog() {
    this.prestationSelectionnee.set({
      id: 0, typeEvenement: 'MARIAGE', dateEvenement: new Date(), lieu: '', description: '', clientId: null
    });
    this.dialogVisible.set(true);
  }

  modifierPrestation(prestation: PrestationResponse) {
    this.prestationSelectionnee.set({
      id: prestation.id,
      typeEvenement: prestation.typeEvenement,
      dateEvenement: new Date(prestation.dateEvenement),
      lieu: prestation.lieu,
      description: prestation.description,
      clientId: prestation.clientId
    });
    this.dialogVisible.set(true);
  }

  supprimerPrestation(id: number) {
    this.prestationApiService.delete(id).subscribe({
      next: () => this.chargerPrestations(),
      error: (err) => console.error('Erreur suppression prestation', err)
    });
  }

  mettreAJourChamp(champ: keyof PrestationForm, valeur: any) {
    this.prestationSelectionnee.update(p => ({ ...p, [champ]: valeur }));
  }

  sauvegarder() {
    const p = this.prestationSelectionnee();
    if (!p.clientId) {
      alert('Veuillez sélectionner un client');
      return;
    }

    const request: PrestationRequest = {
      typeEvenement: p.typeEvenement,
      dateEvenement: this.toIsoDate(p.dateEvenement),
      lieu: p.lieu,
      description: p.description,
      clientId: p.clientId
    };

    const requete = p.id === 0
      ? this.prestationApiService.create(request)
      : this.prestationApiService.update(p.id, request);

    requete.subscribe({
      next: () => {
        this.dialogVisible.set(false);
        this.chargerPrestations();
      },
      error: (err) => console.error('Erreur sauvegarde prestation', err)
    });
  }

  getCouleurEvenement(type: string) {
    switch (type) {
      case 'MARIAGE': return 'contrast';
      case 'BAPTEME': return 'info';
      case 'CEREMONIE': return 'success';
      case 'ANNIVERSAIRE': return 'warn';
      default: return 'secondary';
    }
  }

  private toIsoDate(date: Date): string {
    const mois = `${date.getMonth() + 1}`.padStart(2, '0');
    const jour = `${date.getDate()}`.padStart(2, '0');
    return `${date.getFullYear()}-${mois}-${jour}`;
  }
}

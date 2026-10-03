import { Component, signal, computed, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { ChipModule } from 'primeng/chip';
import { DatePickerModule } from 'primeng/datepicker';
import { HistoriqueApiService, HistoriqueResponse } from '../../services/api/historique-api';

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
export class HistoriqueComponent implements OnInit {

  historiques = signal<HistoriqueResponse[]>([]);
  dateFiltre = signal<Date | null>(null);

  historiquesFiltres = computed(() => {
    const date = this.dateFiltre();
    if (!date) {
      return this.historiques();
    }
    return this.historiques().filter(h =>
      new Date(h.dateAction).toDateString() === date.toDateString()
    );
  });

  constructor(private historiqueApiService: HistoriqueApiService) {}

  ngOnInit(): void {
    this.chargerHistorique();
  }

  chargerHistorique() {
    this.historiqueApiService.findAll().subscribe({
      next: (data) => this.historiques.set(data),
      error: (err) => console.error('Erreur chargement historique', err)
    });
  }

  reinitialiserFiltre() {
    this.dateFiltre.set(null);
  }
}

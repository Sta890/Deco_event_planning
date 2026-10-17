import { Component, signal, computed, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DashboardApiService, DashboardResponse } from '../../../services/api/dashboard-api';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css'
})
export class DashboardComponent implements OnInit {

  data = signal<DashboardResponse | null>(null);

  stats = computed(() => {
    const d = this.data();
    if (!d) {
      return [];
    }
    return [
      { label: 'Total Clients', valeur: d.totalClients, icon: 'pi pi-users', couleur: '#6366f1' },
      { label: 'Événements Décorés', valeur: d.totalPrestations, icon: 'pi pi-sparkles', couleur: '#22c55e' },
      { label: 'Devis En Attente', valeur: d.devisEnAttente, icon: 'pi pi-file', couleur: '#f59e0b' },
      { label: 'Factures En Retard', valeur: d.facturesEnRetard, icon: 'pi pi-exclamation-triangle', couleur: '#ef4444' },
    ];
  });

  constructor(private dashboardApiService: DashboardApiService) {}

  ngOnInit(): void {
    this.dashboardApiService.getStats().subscribe({
      next: (data) => this.data.set(data),
      error: (err) => console.error('Erreur chargement dashboard', err)
    });
  }
}

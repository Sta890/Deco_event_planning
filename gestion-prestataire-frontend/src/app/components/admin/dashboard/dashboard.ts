import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css'
})
export class DashboardComponent {

 stats = signal([
  { label: 'Total Clients', valeur: 24, icon: 'pi pi-users', couleur: '#6366f1' },
  { label: 'Événements Décorés', valeur: 58, icon: 'pi pi-sparkles', couleur: '#22c55e' },
  { label: 'Devis En Attente', valeur: 12, icon: 'pi pi-file', couleur: '#f59e0b' },
  { label: 'Paiements Reçus', valeur: 43, icon: 'pi pi-credit-card', couleur: '#3b82f6' },
]);

activitesRecentes = signal([
  { action: 'Nouveau client ajouté', detail: 'Mariage Famille Dupont', date: '21/05/2026', icon: 'pi pi-user-plus', couleur: '#6366f1' },
  { action: 'Devis créé', detail: 'Baptême Bébé Sarah — 850 XAF', date: '20/05/2026', icon: 'pi pi-file', couleur: '#f59e0b' },
  { action: 'Facture générée', detail: 'Cérémonie Hôtel Renaissance — 3500 XAF', date: '19/05/2026', icon: 'pi pi-file-edit', couleur: '#22c55e' },
  { action: 'Paiement reçu', detail: 'Mariage Boutique Éclat — 850 XAF', date: '18/05/2026', icon: 'pi pi-credit-card', couleur: '#3b82f6' },
  { action: 'Facture en retard', detail: 'Anniversaire VIP FAC-2026-002', date: '17/05/2026', icon: 'pi pi-exclamation-triangle', couleur: '#ef4444' },
]);

  getCouleurStatut(statut: string) {
    return statut === 'Actif' ? '#22c55e' : '#ef4444';
  }
}
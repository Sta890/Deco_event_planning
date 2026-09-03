import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ButtonModule } from 'primeng/button';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterModule, ButtonModule],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  menuItems = [
    { label: 'Clients', route: '/clients', icon: 'pi pi-users' },
    { label: 'Devis', route: '/devis', icon: 'pi pi-file' },
    { label: 'Factures', route: '/factures', icon: 'pi pi-file-edit' },
    { label: 'Paiements', route: '/paiements', icon: 'pi pi-credit-card' },
    { label: 'Historique', route: '/historique', icon: 'pi pi-history' },
  ];
}
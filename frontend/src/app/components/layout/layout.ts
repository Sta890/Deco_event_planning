import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ButtonModule } from 'primeng/button';
import { AuthService } from '../../services/auth';  

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [CommonModule, RouterModule, ButtonModule],
  templateUrl: './layout.html',
  styleUrl: './layout.css'
})
export class LayoutComponent {

  menuItems = [
    { label: 'Clients', route: '/prestataire/clients', icon: 'pi pi-users' },
    { label: 'Prestations', route: '/prestataire/prestations', icon: 'pi pi-sparkles' },
    { label: 'Articles', route: '/prestataire/articles', icon: 'pi pi-box' },
    { label: 'Devis', route: '/prestataire/devis', icon: 'pi pi-file' },
    { label: 'Factures', route: '/prestataire/factures', icon: 'pi pi-file-edit' },
    { label: 'Paiements', route: '/prestataire/paiements', icon: 'pi pi-credit-card' },
    { label: 'Historique', route: '/prestataire/historique', icon: 'pi pi-history' },
  ];

  constructor(private authService: AuthService) {}

  seDeconnecter() {
    this.authService.seDeconnecter();
  }

  getEmail() {
    return this.authService.getUtilisateur()?.email ?? '';
  }
}
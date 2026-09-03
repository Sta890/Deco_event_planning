import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ButtonModule } from 'primeng/button';
import { BadgeModule } from 'primeng/badge';
import { AuthService } from '../../../services/auth';

@Component({
  selector: 'app-client-layout',
  standalone: true,
  imports: [CommonModule, RouterModule, ButtonModule, BadgeModule],
  templateUrl: './layout.html',
  styleUrl: './layout.css'
})
export class ClientLayoutComponent {

  menuItems = [
    { label: 'Accueil', route: '/client/accueil', icon: 'pi pi-home' },
    { label: 'Catalogue', route: '/client/catalogue', icon: 'pi pi-th-large' },
    { label: 'Mon Compte', route: '/client/mon-compte', icon: 'pi pi-user' },
  ];

  constructor(private authService: AuthService) {}

  seDeconnecter() {
    this.authService.seDeconnecter();
  }

  getEmail() {
    return this.authService.getUtilisateur()?.email ?? '';
  }
}
import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ButtonModule } from 'primeng/button';
import { AuthService } from '../../../services/auth';

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [CommonModule, RouterModule, ButtonModule],
  templateUrl: './layout.html',
  styleUrl: './layout.css'
})
export class AdminLayoutComponent {

  menuItems = [
    { label: 'Dashboard', route: '/admin/dashboard', icon: 'pi pi-chart-bar' },
  ];

  constructor(private authService: AuthService) {}

  seDeconnecter() {
    this.authService.seDeconnecter();
  }

  getEmail() {
    return this.authService.getUtilisateur()?.email ?? '';
  }
}
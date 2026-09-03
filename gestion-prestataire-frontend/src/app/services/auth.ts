import { Injectable, signal, PLATFORM_ID, Inject } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { Router } from '@angular/router';
import { AuthApiService, AuthResponse } from './api/auth-api';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private utilisateurConnecte = signal<AuthResponse | null>(null);

  constructor(
    private router: Router,
    private authApiService: AuthApiService,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {
    if (isPlatformBrowser(this.platformId)) {
      const stored = localStorage.getItem('user');
      if (stored) {
        this.utilisateurConnecte.set(JSON.parse(stored));
      }
    }
  }

  seConnecter(email: string, motDePasse: string): void {
    this.authApiService.login({ email, motDePasse }).subscribe({
      next: (response) => {
        if (isPlatformBrowser(this.platformId)) {
          localStorage.setItem('token', response.token);
          localStorage.setItem('user', JSON.stringify(response));
        }
        this.utilisateurConnecte.set(response);

        if (response.role === 'ADMIN') {
          this.router.navigate(['/admin/dashboard']);
        } else if (response.role === 'PRESTATAIRE') {
          this.router.navigate(['/prestataire/clients']);
        } else {
          this.router.navigate(['/client/accueil']);
        }
      },
      error: (err) => {
        console.error('Erreur login', err);
      }
    });
  }

  seDeconnecter() {
    if (isPlatformBrowser(this.platformId)) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
    }
    this.utilisateurConnecte.set(null);
    this.router.navigate(['/login']);
  }

  getUtilisateur() {
    return this.utilisateurConnecte();
  }

  estConnecte() {
    return this.utilisateurConnecte() !== null;
  }
}
import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const utilisateur = authService.getUtilisateur();

  // Non connecté → login
  if (!utilisateur) {
    router.navigate(['/login']);
    return false;
  }

  const url = state.url;

  // Zone admin → seulement ADMIN
  if (url.startsWith('/admin') && utilisateur.role !== 'ADMIN') {
    router.navigate(['/login']);
    return false;
  }

  // Zone prestataire → seulement PRESTATAIRE
  if (url.startsWith('/prestataire') && utilisateur.role !== 'PRESTATAIRE') {
    router.navigate(['/login']);
    return false;
  }

  // Zone client → seulement CLIENT
  if (url.startsWith('/client') && utilisateur.role !== 'CLIENT') {
    router.navigate(['/login']);
    return false;
  }

  return true;
};
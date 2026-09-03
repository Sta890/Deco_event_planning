import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { InputTextModule } from 'primeng/inputtext';
import { ButtonModule } from 'primeng/button';
import { PasswordModule } from 'primeng/password';
import { MessageModule } from 'primeng/message';
import { AuthApiService } from '../../services/api/auth-api';

@Component({
  selector: 'app-inscription',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, InputTextModule, ButtonModule, PasswordModule, MessageModule],
  templateUrl: './inscription.html',
  styleUrl: './inscription.css'
})
export class InscriptionComponent {

  etape = signal<1 | 2>(1);
  erreur = signal('');
  chargement = signal(false);

  formulaire = signal({
    nom: '',
    telephone: '',
    adresse: '',
    email: '',
    motDePasse: '',
    confirmerMotDePasse: '',
  });

  constructor(
    private authApiService: AuthApiService,
    private router: Router
  ) {}

  mettreAJour(champ: string, valeur: string) {
    this.formulaire.update(f => ({ ...f, [champ]: valeur }));
  }

  etapeSuivante() {
    const f = this.formulaire();
    if (!f.nom || !f.telephone || !f.email) {
      this.erreur.set('Veuillez remplir tous les champs obligatoires !');
      return;
    }
    this.erreur.set('');
    this.etape.set(2);
  }

  sinscrire() {
    const f = this.formulaire();

    if (!f.motDePasse || !f.confirmerMotDePasse) {
      this.erreur.set('Veuillez remplir tous les champs !');
      return;
    }
    if (f.motDePasse !== f.confirmerMotDePasse) {
      this.erreur.set('Les mots de passe ne correspondent pas !');
      return;
    }
    if (f.motDePasse.length < 4) {
      this.erreur.set('Le mot de passe doit contenir au moins 4 caractères !');
      return;
    }

    this.erreur.set('');
    this.chargement.set(true);

    // ✅ Appel réel au backend
    this.authApiService.inscrire({
      nom: f.nom,
      email: f.email,
      motDePasse: f.motDePasse,
      telephone: f.telephone,
      adresse: f.adresse
    }).subscribe({
      next: (response) => {
        this.chargement.set(false);
        // Stocke le token et redirige vers le login
        localStorage.setItem('token', response.token);
        localStorage.setItem('user', JSON.stringify(response));
        this.router.navigate(['/client/accueil']);
      },
      error: (err) => {
        this.chargement.set(false);
        if (err.status === 400) {
          this.erreur.set('Email déjà utilisé !');
        } else {
          this.erreur.set('Erreur lors de l\'inscription. Réessayez !');
        }
        console.error('Erreur inscription', err);
      }
    });
  }
}
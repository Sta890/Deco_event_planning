import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { InputTextModule } from 'primeng/inputtext';
import { ButtonModule } from 'primeng/button';
import { PasswordModule } from 'primeng/password';
import { MessageModule } from 'primeng/message';
import { AuthService } from '../../services/auth';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, InputTextModule, ButtonModule, PasswordModule, MessageModule],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class LoginComponent {

  email = signal('');
  motDePasse = signal('');
  erreur = signal('');
  chargement = signal(false);

  constructor(private authService: AuthService) {}

  seConnecter() {
    if (!this.email() || !this.motDePasse()) {
      this.erreur.set('Veuillez remplir tous les champs !');
      return;
    }
    this.chargement.set(true);
    this.erreur.set('');
    this.authService.seConnecter(this.email(), this.motDePasse());
    this.chargement.set(false);
  }
}
import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { TagModule } from 'primeng/tag';
import { MessageModule } from 'primeng/message';
import { finalize, forkJoin } from 'rxjs';
import { AuthService } from '../../../services/auth';
import { DevisApiService, DevisResponse } from '../../../services/api/devis-api';
import { FactureApiService, FactureResponse } from '../../../services/api/facture-api';
import { EnumLabelPipe } from '../../../shared/pipes/enum-label.pipe';

@Component({
  selector: 'app-mon-compte',
  standalone: true,
  imports: [CommonModule, RouterModule, ButtonModule, TagModule, MessageModule, EnumLabelPipe],
  templateUrl: './mon-compte.html',
  styleUrl: './mon-compte.css'
})
export class MonCompteComponent implements OnInit {

  ongletActif = signal<'devis' | 'factures'>('devis');

  mesDevis = signal<DevisResponse[]>([]);
  mesFactures = signal<FactureResponse[]>([]);

  chargement = signal(false);
  erreur = signal<string | null>(null);

  constructor(
    private authService: AuthService,
    private devisApiService: DevisApiService,
    private factureApiService: FactureApiService
  ) {}

  ngOnInit(): void {
    this.chargerDonnees();
  }

  /**
   * Recharge devis et factures.
   *
   * Le prestataire peut générer une facture pendant que le client a la page
   * ouverte : les données étaient donc chargées une seule fois au démarrage et
   * n'arrivaient jamais sans rechargement complet de la page. D'où le bouton
   * et le rechargement au changement d'onglet.
   */
  chargerDonnees(): void {
    if (this.chargement()) {
      return;
    }

    this.chargement.set(true);
    this.erreur.set(null);

    // forkJoin et non deux subscribe séparés : les deux requêtes sont lancées
    // ensemble et l'indicateur de chargement ne retombe qu'une fois, quand les
    // deux sont terminées. Avec deux finalize distincts, le premier retour
    // masquerait le spinner alors que l'autre requête est encore en vol.
    forkJoin({
      devis: this.devisApiService.getMesDevis(),
      factures: this.factureApiService.getMesFactures()
    })
      .pipe(finalize(() => this.chargement.set(false)))
      .subscribe({
        next: ({ devis, factures }) => {
          this.mesDevis.set(devis);
          this.mesFactures.set(factures);
        },
        error: (err) => {
          console.error('Erreur chargement mes devis/factures', err);
          this.erreur.set('Impossible de charger vos devis et factures');
        }
      });
  }

  changerOnglet(onglet: 'devis' | 'factures'): void {
    this.ongletActif.set(onglet);
    // Le client va regarder ses factures pour vérifier qu'une nouvelle est
    // arrivée : on rafraîchit à ce moment-là plutôt que de l'obliger à cliquer.
    this.chargerDonnees();
  }

  getEmail() {
    return this.authService.getUtilisateur()?.email ?? '';
  }

  getCouleurStatut(statut: string) {
    switch (statut?.toUpperCase()) {
      case 'VALIDE': case 'PAYE': return 'success';
      case 'EN_ATTENTE': return 'warn';
      case 'REFUSE': case 'EN_RETARD': return 'danger';
      default: return 'secondary';
    }
  }
}
import { Component, signal, computed, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { TagModule } from 'primeng/tag';
import { DatePickerModule } from 'primeng/datepicker';
import { InputNumberModule } from 'primeng/inputnumber';
import { SelectModule } from 'primeng/select';
import { MessageModule } from 'primeng/message';
import { PaiementApiService, PaiementRequest, PaiementResponse } from '../../services/api/paiement-api';
import { FactureApiService, FactureResponse } from '../../services/api/facture-api';
import { EnumLabelPipe } from '../../shared/pipes/enum-label.pipe';
import {
  ModePaiement,
  PrimeSeverity,
  MODE_PAIEMENT,
  MODE_PAIEMENT_OPTIONS,
  MODE_PAIEMENT_SEVERITY,
  STATUT_FACTURE,
} from '../../shared/enums';

interface PaiementForm {
  id: number;
  factureId: number | null;
  montant: number | null;
  modePaiement: ModePaiement;
  datePaiement: Date;
}

@Component({
  selector: 'app-paiements',
  standalone: true,
  imports: [
    CommonModule, FormsModule, TableModule, ButtonModule,
    DialogModule, TagModule, DatePickerModule, InputNumberModule, SelectModule,
    MessageModule, EnumLabelPipe
  ],
  templateUrl: './paiements.html',
  styleUrl: './paiements.css'
})
export class PaiementsComponent implements OnInit {

  paiements = signal<PaiementResponse[]>([]);
  factures = signal<FactureResponse[]>([]);

  dialogVisible = signal(false);
  erreur = signal('');

  modePaiementOptions = MODE_PAIEMENT_OPTIONS;

  paiementSelectionne = signal<PaiementForm>({
    id: 0, factureId: null, montant: null, modePaiement: MODE_PAIEMENT.CASH, datePaiement: new Date()
  });

  /** Factures déjà réglées : un paiement est unique et intégral par facture. */
  private facturesDejaPayees = computed<Set<number>>(() => {
    const ids = new Set<number>();
    for (const p of this.paiements()) {
      ids.add(p.factureId);
    }
    for (const f of this.factures()) {
      if (f.statutFacture === STATUT_FACTURE.PAYE) {
        ids.add(f.id);
      }
    }
    return ids;
  });

  /** Seules les factures non réglées sont proposées (le backend les refuse sinon). */
  factureOptions = computed(() =>
    this.factures()
      .filter(f => !this.facturesDejaPayees().has(f.id))
      .map(f => ({
        label: `Facture #${f.id} — ${f.clientNom} — ${this.formaterMontant(f.montantTotal)}`,
        value: f.id
      }))
  );

  factureSelectionnee = computed<FactureResponse | null>(() => {
    const id = this.paiementSelectionne().factureId;
    return id === null ? null : (this.factures().find(f => f.id === id) ?? null);
  });

  /** Montant exact attendu par le backend pour la facture choisie. */
  montantAttendu = computed<number | null>(() => this.factureSelectionnee()?.montantTotal ?? null);

  /**
   * Le backend impose un paiement unique et intégral par facture et compare
   * avec BigDecimal.compareTo(), c'est-à-dire sans se soucier de l'échelle
   * (100.0 == 100.00). On reproduit cette sémantique en comparant les
   * valeurs arrondies à 2 décimales, sans aucune tolérance.
   */
  montantValide = computed(() => {
    const attendu = this.montantAttendu();
    const saisi = this.paiementSelectionne().montant;
    if (attendu === null || saisi === null || saisi === undefined) {
      return false;
    }
    return this.arrondir(saisi) === this.arrondir(attendu);
  });

  /** Désactive "Sauvegarder" tant que la facture ou le montant ne sont pas valides. */
  formulaireValide = computed(() =>
    this.paiementSelectionne().factureId !== null && this.montantValide()
  );

  constructor(
    private paiementApiService: PaiementApiService,
    private factureApiService: FactureApiService
  ) {}

  ngOnInit(): void {
    this.chargerPaiements();
    this.chargerFactures();
  }

  chargerPaiements() {
    this.paiementApiService.findAll().subscribe({
      next: (data) => this.paiements.set(data),
      error: (err) => console.error('Erreur chargement paiements', err)
    });
  }

  chargerFactures() {
    this.factureApiService.findAll().subscribe({
      next: (data) => this.factures.set(data),
      error: (err) => console.error('Erreur chargement factures', err)
    });
  }

  ouvrirDialog() {
    this.erreur.set('');
    this.paiementSelectionne.set({
      id: 0, factureId: null, montant: null, modePaiement: MODE_PAIEMENT.CASH, datePaiement: new Date()
    });
    this.dialogVisible.set(true);
  }

  supprimerPaiement(id: number) {
    this.paiementApiService.delete(id).subscribe({
      next: () => {
        this.chargerPaiements();
        this.chargerFactures();
      },
      error: (err) => console.error('Erreur suppression paiement', err)
    });
  }

  mettreAJourChamp(champ: keyof PaiementForm, valeur: any) {
    this.erreur.set('');
    this.paiementSelectionne.update(p => ({ ...p, [champ]: valeur }));
  }

  /**
   * Sélection d'une facture : le montant est pré-rempli avec le montant exact
   * de la facture, l'utilisateur n'a plus qu'à confirmer.
   */
  selectionnerFacture(factureId: number | null) {
    this.erreur.set('');
    const facture = factureId === null ? null : this.factures().find(f => f.id === factureId);
    this.paiementSelectionne.update(p => ({
      ...p,
      factureId,
      montant: facture ? facture.montantTotal : null
    }));
  }

  sauvegarder() {
    const p = this.paiementSelectionne();
    this.erreur.set('');

    if (!p.factureId) {
      this.erreur.set('Veuillez sélectionner une facture');
      return;
    }

    const attendu = this.montantAttendu();
    if (attendu === null) {
      this.erreur.set('Facture introuvable, rechargez la liste des factures');
      return;
    }

    if (p.montant === null || p.montant === undefined) {
      this.erreur.set('Veuillez saisir le montant du paiement');
      return;
    }

    // Contrôle miroir de PaiementService.create() : évite un aller-retour
    // et un message d'erreur serveur pour une simple saisie.
    if (this.arrondir(p.montant) !== this.arrondir(attendu)) {
      this.erreur.set(
        `Le montant du paiement (${this.formaterMontant(p.montant)}) ne correspond pas `
        + `au montant de la facture #${p.factureId} (${this.formaterMontant(attendu)})`
      );
      return;
    }

    const request: PaiementRequest = {
      factureId: p.factureId,
      montant: attendu,
      modePaiement: p.modePaiement,
      datePaiement: this.toIsoDate(p.datePaiement)
    };

    this.paiementApiService.create(request).subscribe({
      next: () => {
        this.dialogVisible.set(false);
        this.chargerPaiements();
        this.chargerFactures();
      },
      error: (err) => {
        console.error('Erreur sauvegarde paiement', err);
        this.erreur.set(err?.error?.message ?? 'Impossible d\'enregistrer le paiement');
      }
    });
  }

  getCouleurMode(mode: ModePaiement): PrimeSeverity {
    return MODE_PAIEMENT_SEVERITY[mode] ?? 'secondary';
  }

  formaterMontant(montant: number): string {
    return new Intl.NumberFormat('fr-FR', {
      minimumFractionDigits: 0,
      maximumFractionDigits: 2
    }).format(montant) + ' XAF';
  }

  private arrondir(valeur: number): number {
    return Math.round((valeur + Number.EPSILON) * 100) / 100;
  }

  private toIsoDate(date: Date): string {
    const mois = `${date.getMonth() + 1}`.padStart(2, '0');
    const jour = `${date.getDate()}`.padStart(2, '0');
    return `${date.getFullYear()}-${mois}-${jour}`;
  }
}

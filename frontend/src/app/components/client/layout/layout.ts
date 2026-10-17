import { Component, OnInit, OnDestroy, signal, computed } from '@angular/core';
import { RouterModule, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ButtonModule } from 'primeng/button';
import { TagModule } from 'primeng/tag';
import { TooltipModule } from 'primeng/tooltip';
import { EnumLabelPipe } from '../../../shared/pipes/enum-label.pipe';
import {
  NotificationApiService,
  NotificationResponse,
} from '../../../services/api/notification-api';
import {
  PrimeSeverity,
  TYPE_NOTIFICATION_SEVERITY,
  TypeNotification,
} from '../../../shared/enums';
import { AuthService } from '../../../services/auth';

/** Intervalle de rafraîchissement du badge, en millisecondes. */
const RAFRAICHISSEMENT_MS = 30_000;

@Component({
  selector: 'app-client-layout',
  standalone: true,
  imports: [CommonModule, RouterModule, ButtonModule, TagModule, TooltipModule, EnumLabelPipe],
  templateUrl: './layout.html',
  styleUrl: './layout.css',
})
export class ClientLayoutComponent implements OnInit, OnDestroy {

  menuItems = [
    { label: 'Accueil', route: '/client/accueil', icon: 'pi pi-home' },
    { label: 'Catalogue', route: '/client/catalogue', icon: 'pi pi-th-large' },
    { label: 'Mon Compte', route: '/client/mon-compte', icon: 'pi pi-user' },
  ];

  notifications = signal<NotificationResponse[]>([]);
  panneauOuvert = signal(false);

  /** Nombre de notifications non lues, affiché en badge sur la cloche. */
  nbNonLues = computed(() => this.notifications().filter(n => !n.lue).length);

  private timer: ReturnType<typeof setInterval> | null = null;

  constructor(
    private authService: AuthService,
    private notificationApiService: NotificationApiService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.chargerNotifications();

    // Le client garde souvent l'onglet ouvert pendant une quite durée : sans
    // rafraîchissement périodique, la cloche ne refléterait l'arrivée d'une
    // notification qu'après un rechargement de la page.
    this.timer = setInterval(() => this.chargerNotifications(false), RAFRAICHISSEMENT_MS);
  }

  ngOnDestroy(): void {
    if (this.timer !== null) {
      clearInterval(this.timer);
      this.timer = null;
    }
  }

  chargerNotifications(ouvrirSiNonVide = false) {
    this.notificationApiService.findAll().subscribe({
      next: (data) => {
        this.notifications.set(data);
        // Le panneau ne s'ouvre pas tout seul : il s'ouvre au clic. Ce paramètre
        // sert seulement à ce qu'un appel manuel puisse le faire si besoin.
        if (ouvrirSiNonVide) {
          this.panneauOuvert.set(true);
        }
      },
      error: (err) => console.error('Erreur chargement notifications', err)
    });
  }

  basculerPanneau() {
    this.panneauOuvert.update(v => !v);
    if (this.panneauOuvert()) {
      this.chargerNotifications();
    }
  }

  marquerCommeLue(notification: NotificationResponse) {
    if (notification.lue) {
      this.ouvrirMonCompte();
      return;
    }

    this.notificationApiService.marquerCommeLue(notification.id).subscribe({
      next: () => {
        this.notifications.update(list =>
          list.map(n => (n.id === notification.id ? { ...n, lue: true } : n))
        );
        this.ouvrirMonCompte();
      },
      error: (err) => console.error('Erreur marquage notification', err)
    });
  }

  toutMarquerLu() {
    this.notificationApiService.marquerToutesCommeLues().subscribe({
      next: () => this.notifications.update(list => list.map(n => ({ ...n, lue: true }))),
      error: (err) => console.error('Erreur marquage notifications', err)
    });
  }

  /** Aiguille l'icône de la notification vers l'écran concerné. */
  couleurNotification(type: TypeNotification): PrimeSeverity {
    return TYPE_NOTIFICATION_SEVERITY[type] ?? 'secondary';
  }

  private ouvrirMonCompte() {
    this.panneauOuvert.set(false);
    this.router.navigate(['/client/mon-compte']);
  }

  seDeconnecter() {
    if (this.timer !== null) {
      clearInterval(this.timer);
      this.timer = null;
    }
    this.authService.seDeconnecter();
  }

  getEmail() {
    return this.authService.getUtilisateur()?.email ?? '';
  }
}
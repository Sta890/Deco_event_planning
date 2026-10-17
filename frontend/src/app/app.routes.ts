import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login';
import { LayoutComponent } from './components/layout/layout';
import { AdminLayoutComponent } from './components/admin/layout/layout';
import { DashboardComponent } from './components/admin/dashboard/dashboard';
import { ClientsComponent } from './components/clients/clients';
import { PrestationsComponent } from './components/prestations/prestations';
import { ArticlesComponent } from './components/articles/articles';
import { DevisComponent } from './components/devis/devis';
import { FacturesComponent } from './components/factures/factures';
import { PaiementsComponent } from './components/paiements/paiements';
import { HistoriqueComponent } from './components/historique/historique';
import { ClientLayoutComponent } from './components/client/layout/layout';
import { AccueilComponent } from './components/client/accueil/accueil';
import { CatalogueComponent } from './components/client/catalogue/catalogue';
import { PanierComponent } from './components/client/panier/panier';
import { DemandeDevisComponent } from './components/client/demande-devis/demande-devis';
import { MonCompteComponent } from './components/client/mon-compte/mon-compte';
import { InscriptionComponent } from './components/inscription/inscription';

import { authGuard } from './guards/auth-guard';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'inscription', component: InscriptionComponent },
  {
    path: 'admin',
    component: AdminLayoutComponent,
    canActivate: [authGuard],        // ← ici
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', component: DashboardComponent },
    ]
  },
  {
    path: 'prestataire',
    component: LayoutComponent,
    canActivate: [authGuard],        // ← ici
    children: [
      { path: '', redirectTo: 'clients', pathMatch: 'full' },
      { path: 'clients', component: ClientsComponent },
      { path: 'prestations', component: PrestationsComponent },
      { path: 'articles', component: ArticlesComponent },
      { path: 'devis', component: DevisComponent },
      { path: 'factures', component: FacturesComponent },
      { path: 'paiements', component: PaiementsComponent },
      { path: 'historique', component: HistoriqueComponent },
    ]
  },
  {
    path: 'client',
    component: ClientLayoutComponent,
    canActivate: [authGuard],        // ← ici
    children: [
      { path: '', redirectTo: 'accueil', pathMatch: 'full' },
      { path: 'accueil', component: AccueilComponent },
      { path: 'catalogue', component: CatalogueComponent },
      { path: 'panier', component: PanierComponent },
      { path: 'demande-devis', component: DemandeDevisComponent },
      { path: 'mon-compte', component: MonCompteComponent },
    ]
  },
  { path: '**', redirectTo: 'login' }
];
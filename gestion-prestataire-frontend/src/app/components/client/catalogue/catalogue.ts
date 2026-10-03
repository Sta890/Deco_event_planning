import { Component, signal, computed, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { TagModule } from 'primeng/tag';
import { SelectModule } from 'primeng/select';
import { FormsModule } from '@angular/forms';
import { CatalogueApiService } from '../../../services/api/catalogue-api';
import { ArticleResponse } from '../../../services/api/article-api';
import { PanierApiService, PanierResponse } from '../../../services/api/panier-api';
import { AuthService } from '../../../services/auth';
import { EnumLabelPipe } from '../../../shared/pipes/enum-label.pipe';

@Component({
  selector: 'app-catalogue',
  standalone: true,
  imports: [CommonModule, RouterModule, ButtonModule, TagModule, SelectModule, FormsModule, EnumLabelPipe],
  templateUrl: './catalogue.html',
  styleUrl: './catalogue.css'
})
export class CatalogueComponent implements OnInit {

  filtreActif = signal<string>('TOUS');

  filtreOptions = [
    { label: 'Tous', value: 'TOUS' },
    { label: 'Mariage', value: 'MARIAGE' },
    { label: 'Baptême', value: 'BAPTEME' },
    { label: 'Cérémonie', value: 'CEREMONIE' },
    { label: 'Anniversaire', value: 'ANNIVERSAIRE' },
  ];

  articles = signal<ArticleResponse[]>([]);
  panier = signal<PanierResponse | null>(null);

  articlesFiltres = computed(() => {
    const filtre = this.filtreActif();
    if (filtre === 'TOUS') {
      return this.articles();
    }
    return this.articles().filter(a => a.typeEvenement === filtre || a.typeEvenement === 'AUTRE');
  });

  lignesPanier = computed(() => this.panier()?.lignes ?? []);
  totalPanier = computed(() => this.panier()?.total ?? this.lignesPanier().reduce((acc, l) => acc + l.sousTotal, 0));
  nombreArticles = computed(() => this.lignesPanier().reduce((acc, l) => acc + l.quantite, 0));

  constructor(
    private catalogueApiService: CatalogueApiService,
    private panierApiService: PanierApiService,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit() {
    const type = this.route.snapshot.queryParamMap.get('type');
    if (type) {
      this.filtreActif.set(type);
    }
    this.chargerArticles();
    this.chargerPanierData();
  }

  chargerArticles() {
    this.catalogueApiService.getArticles().subscribe({
      next: (articles) => this.articles.set(articles),
      error: (err) => console.error('Erreur chargement articles', err)
    });
  }

  chargerPanierData() {
    const user = this.authService.getUtilisateur();
    if (!user) {
      return;
    }
    this.panierApiService.getPanier(user.id).subscribe({
      next: (panierData) => this.panier.set(panierData),
      error: (err) => console.error('Erreur chargement panier', err)
    });
  }

  ajouterAuPanier(article: ArticleResponse) {
    const user = this.authService.getUtilisateur();
    if (!user || !user.id) {
      this.router.navigate(['/login']);
      return;
    }

    this.panierApiService.ajouterArticle(user.id, { articleId: article.id, quantite: 1 }).subscribe({
      next: (panierMisAJour) => this.panier.set(panierMisAJour),
      error: (err) => console.error('Erreur ajout au panier', err)
    });
  }

  estDansPanier(idArticle: number): boolean {
    return this.lignesPanier().some(l => l.articleId === idArticle);
  }

  getEmoji(type: string): string {
    switch (type) {
      case 'MARIAGE': return '💍';
      case 'BAPTEME': return '🍼';
      case 'CEREMONIE': return '🎊';
      case 'ANNIVERSAIRE': return '🎂';
      default: return '🎁';
    }
  }

  getCouleurType(type: string) {
    switch (type) {
      case 'MARIAGE': return 'contrast';
      case 'BAPTEME': return 'info';
      case 'CEREMONIE': return 'success';
      case 'ANNIVERSAIRE': return 'warn';
      default: return 'secondary';
    }
  }
}

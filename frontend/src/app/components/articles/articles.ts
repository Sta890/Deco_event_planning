import { Component, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { TagModule } from 'primeng/tag';
import { InputTextModule } from 'primeng/inputtext';
import { InputNumberModule } from 'primeng/inputnumber';
import { SelectModule } from 'primeng/select';
import { ArticleApiService, ArticleRequest, ArticleResponse } from '../../services/api/article-api';
import { EnumLabelPipe } from '../../shared/pipes/enum-label.pipe';

@Component({
  selector: 'app-articles',
  standalone: true,
  imports: [
    CommonModule, FormsModule, TableModule, ButtonModule,
    DialogModule, TagModule, InputTextModule, InputNumberModule, SelectModule, EnumLabelPipe
  ],
  templateUrl: './articles.html',
  styleUrl: './articles.css'
})
export class ArticlesComponent implements OnInit {

  articles = signal<ArticleResponse[]>([]);
  dialogVisible = signal(false);

  typeEvenementOptions = [
    { label: 'Mariage', value: 'MARIAGE' },
    { label: 'Baptême', value: 'BAPTEME' },
    { label: 'Cérémonie', value: 'CEREMONIE' },
    { label: 'Anniversaire', value: 'ANNIVERSAIRE' },
    { label: 'Autre', value: 'AUTRE' },
  ];

  articleSelectionne = signal<ArticleResponse>({
    id: 0, nom: '', description: '', prixUnitaire: 0, typeEvenement: 'AUTRE'
  });

  constructor(private articleApiService: ArticleApiService) {}

  ngOnInit(): void {
    this.chargerArticles();
  }

  chargerArticles() {
    this.articleApiService.findAll().subscribe({
      next: (data) => this.articles.set(data),
      error: (err) => console.error('Erreur chargement articles', err)
    });
  }

  ouvrirDialog() {
    this.articleSelectionne.set({ id: 0, nom: '', description: '', prixUnitaire: 0, typeEvenement: 'AUTRE' });
    this.dialogVisible.set(true);
  }

  modifierArticle(article: ArticleResponse) {
    this.articleSelectionne.set({ ...article });
    this.dialogVisible.set(true);
  }

  supprimerArticle(id: number) {
    this.articleApiService.delete(id).subscribe({
      next: () => this.chargerArticles(),
      error: (err) => console.error('Erreur suppression article', err)
    });
  }

  mettreAJourChamp(champ: keyof ArticleResponse, valeur: any) {
    this.articleSelectionne.update(a => ({ ...a, [champ]: valeur }));
  }

  sauvegarder() {
    const a = this.articleSelectionne();
    const request: ArticleRequest = {
      nom: a.nom,
      description: a.description,
      prixUnitaire: a.prixUnitaire,
      typeEvenement: a.typeEvenement
    };

    const requete = a.id === 0
      ? this.articleApiService.create(request)
      : this.articleApiService.update(a.id, request);

    requete.subscribe({
      next: () => {
        this.dialogVisible.set(false);
        this.chargerArticles();
      },
      error: (err) => console.error('Erreur sauvegarde article', err)
    });
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

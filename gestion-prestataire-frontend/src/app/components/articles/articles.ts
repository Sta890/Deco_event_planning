import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { TagModule } from 'primeng/tag';
import { InputTextModule } from 'primeng/inputtext';
import { InputNumberModule } from 'primeng/inputnumber';
import { SelectModule } from 'primeng/select';
import { Article } from '../../models/article.model';

@Component({
  selector: 'app-articles',
  standalone: true,
  imports: [
    CommonModule, FormsModule, TableModule, ButtonModule,
    DialogModule, TagModule, InputTextModule, InputNumberModule, SelectModule
  ],
  templateUrl: './articles.html',
  styleUrl: './articles.css'
})
export class ArticlesComponent {

  articles = signal<Article[]>([
    { idArticle: 1, nom: 'Chaise dorée', description: 'Chaise dorée style royal', prixUnitaire: 2500, typeEvenement: 'Mariage' },
    { idArticle: 2, nom: 'Table ronde', description: 'Table ronde 8 personnes', prixUnitaire: 15000, typeEvenement: 'Tous' },
    { idArticle: 3, nom: 'Couvert complet', description: 'Set assiette verre couverts', prixUnitaire: 3000, typeEvenement: 'Tous' },
    { idArticle: 4, nom: 'Bouquet floral', description: 'Bouquet décoration table', prixUnitaire: 8000, typeEvenement: 'Mariage' },
    { idArticle: 5, nom: 'Arche florale', description: 'Arche fleurs naturelles', prixUnitaire: 45000, typeEvenement: 'Mariage' },
    { idArticle: 6, nom: 'Nappe brodée', description: 'Nappe blanche brodée', prixUnitaire: 5000, typeEvenement: 'Tous' },
    { idArticle: 7, nom: 'Ballon décoratif', description: 'Pack 50 ballons colorés', prixUnitaire: 7000, typeEvenement: 'Baptême' },
    { idArticle: 8, nom: 'Sono événementielle', description: 'Système son complet', prixUnitaire: 80000, typeEvenement: 'Tous' },
  ]);

  dialogVisible = signal(false);

  typeEvenementOptions = [
    { label: 'Tous', value: 'Tous' },
    { label: 'Mariage', value: 'Mariage' },
    { label: 'Baptême', value: 'Baptême' },
    { label: 'Cérémonie', value: 'Cérémonie' },
    { label: 'Anniversaire', value: 'Anniversaire' },
    { label: 'Autre', value: 'Autre' },
  ];

  articleSelectionne = signal<Article>({
    idArticle: 0, nom: '', description: '', prixUnitaire: 0, typeEvenement: 'Tous'
  });

  ouvrirDialog() {
    this.articleSelectionne.set({ idArticle: 0, nom: '', description: '', prixUnitaire: 0, typeEvenement: 'Tous' });
    this.dialogVisible.set(true);
  }

  modifierArticle(article: Article) {
    this.articleSelectionne.set({ ...article });
    this.dialogVisible.set(true);
  }

  supprimerArticle(id: number) {
    this.articles.update((list: Article[]) => list.filter(a => a.idArticle !== id));
  }

  mettreAJourChamp(champ: keyof Article, valeur: any) {
    this.articleSelectionne.update(a => ({ ...a, [champ]: valeur }));
  }

  sauvegarder() {
    const a = this.articleSelectionne();
    if (a.idArticle === 0) {
      this.articles.update((list: Article[]) => [...list, { ...a, idArticle: list.length + 1 }]);
    } else {
      this.articles.update((list: Article[]) => list.map(x => x.idArticle === a.idArticle ? { ...a } : x));
    }
    this.dialogVisible.set(false);
  }

  getCouleurType(type: string) {
    switch (type) {
      case 'Mariage': return 'contrast';
      case 'Baptême': return 'info';
      case 'Cérémonie': return 'success';
      case 'Anniversaire': return 'warn';
      case 'Tous': return 'secondary';
      default: return 'secondary';
    }
  }
}
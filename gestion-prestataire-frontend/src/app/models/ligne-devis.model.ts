import { Article } from './article.model';

export interface LigneDevis {
  idArticle: number;
  nom: string;
  quantite: number;
  prixUnitaire: number;
  sousTotal: number;
}
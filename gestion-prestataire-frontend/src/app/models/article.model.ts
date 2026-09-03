export interface Article {
  idArticle: number;
  nom: string;
  description: string;
  prixUnitaire: number;
  typeEvenement: 'Mariage' | 'Baptême' | 'Cérémonie' | 'Anniversaire' | 'Autre' | 'Tous';
}
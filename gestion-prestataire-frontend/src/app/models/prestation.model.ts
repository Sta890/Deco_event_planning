export interface Prestation {
  idPrestation: number;
  typeEvenement: 'Mariage' | 'Baptême' | 'Cérémonie' | 'Anniversaire' | 'Autre';
  dateEvenement: Date;
  lieu: string;
  description: string;
  idClient: number;
}
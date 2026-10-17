import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface LignePanierResponse {
  id: number;
  articleId: number;
  articleNom: string;
  prixUnitaire: number;
  quantite: number;
  sousTotal: number;
  typeEvenement: string;
}

export interface PanierResponse {
  id: number;
  utilisateurId: number;
  lignes: LignePanierResponse[];
  total: number;
}

export interface AjouterArticlePanierRequest {
  articleId: number;
  quantite: number;
}

@Injectable({
  providedIn: 'root'
})
export class PanierApiService {

  private url = `${environment.apiUrl}/client/panier`;

  constructor(private http: HttpClient) {}

  getPanier(utilisateurId: number): Observable<PanierResponse> {
    return this.http.get<PanierResponse>(`${this.url}/${utilisateurId}`);
  }

  ajouterArticle(utilisateurId: number, request: { articleId: number; quantite: number }): Observable<PanierResponse> {
    return this.http.post<PanierResponse>(`${this.url}/${utilisateurId}/ajouter`, request);
  }

  modifierQuantite(utilisateurId: number, ligneId: number, quantite: number): Observable<PanierResponse> {
    return this.http.put<PanierResponse>(`${this.url}/${utilisateurId}/ligne/${ligneId}?quantite=${quantite}`, null);
  }

  supprimerArticle(utilisateurId: number, ligneId: number): Observable<PanierResponse> {
    return this.http.delete<PanierResponse>(`${this.url}/${utilisateurId}/ligne/${ligneId}`);
  }

  viderPanier(utilisateurId: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${utilisateurId}/vider`);
  }
}

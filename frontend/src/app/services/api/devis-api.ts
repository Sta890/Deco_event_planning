import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface LigneDevisRequest {
  articleId: number;
  quantite: number;
}

export interface LigneDevisResponse {
  id: number;
  articleId: number;
  articleNom: string;
  quantite: number;
  prixUnitaire: number;
  sousTotal: number;
}

export interface DevisRequest {
  prestationId: number;
  lignes: LigneDevisRequest[];
}

export interface DevisResponse {
  id: number;
  dateCreation: string;
  statutDevis: string;
  montantTotal: number;
  prestationId: number;
  typeEvenement: string;
  clientNom: string;
  lignes: LigneDevisResponse[];
  createdAt: string;
}

@Injectable({
  providedIn: 'root'
})
export class DevisApiService {

  private url = `${environment.apiUrl}/prestataire/devis`;

  constructor(private http: HttpClient) {}

  findAll(): Observable<DevisResponse[]> {
    return this.http.get<DevisResponse[]>(this.url);
  }

  findById(id: number): Observable<DevisResponse> {
    return this.http.get<DevisResponse>(`${this.url}/${id}`);
  }

  findByClientId(clientId: number): Observable<DevisResponse[]> {
    return this.http.get<DevisResponse[]>(`${this.url}/client/${clientId}`);
  }

  getMesDevis(): Observable<DevisResponse[]> {
    return this.http.get<DevisResponse[]>(`${environment.apiUrl}/client/mes-devis`);
  }

  create(request: DevisRequest): Observable<DevisResponse> {
    return this.http.post<DevisResponse>(this.url, request);
  }

  mettreAJourLignes(id: number, lignes: LigneDevisRequest[]): Observable<DevisResponse> {
    return this.http.put<DevisResponse>(`${this.url}/${id}/lignes`, lignes);
  }

  updateStatut(id: number, statut: string): Observable<DevisResponse> {
    const params = new HttpParams().set('statut', statut);
    return this.http.put<DevisResponse>(`${this.url}/${id}/statut`, null, { params });
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}

import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface FactureResponse {
  id: number;
  dateFacture: string;
  montantTotal: number;
  statut: string;
  devisId: number;
  clientNom: string;
  typeEvenement: string;
  createdAt: string;
}

@Injectable({
  providedIn: 'root'
})
export class FactureApiService {

  private url = `${environment.apiUrl}/prestataire/factures`;

  constructor(private http: HttpClient) {}

  findAll(): Observable<FactureResponse[]> {
    return this.http.get<FactureResponse[]>(this.url);
  }

  findById(id: number): Observable<FactureResponse> {
    return this.http.get<FactureResponse>(`${this.url}/${id}`);
  }

  findByClientId(clientId: number): Observable<FactureResponse[]> {
    return this.http.get<FactureResponse[]>(`${this.url}/client/${clientId}`);
  }

  genererDepuisDevis(devisId: number): Observable<FactureResponse> {
    return this.http.post<FactureResponse>(`${this.url}/generer/${devisId}`, null);
  }

  updateStatut(id: number, statut: string): Observable<FactureResponse> {
    const params = new HttpParams().set('statut', statut);
    return this.http.put<FactureResponse>(`${this.url}/${id}/statut`, null, { params });
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}
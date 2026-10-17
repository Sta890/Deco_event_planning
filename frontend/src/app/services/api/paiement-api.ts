import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ModePaiement } from '../../shared/enums';

export interface PaiementRequest {
  factureId: number;
  montant: number;
  /** Doit correspondre à l'enum Java ModePaiement (sérialisé en STRING). */
  modePaiement: ModePaiement;
  datePaiement: string;
}

export interface PaiementResponse {
  id: number;
  datePaiement: string;
  montant: number;
  modePaiement: ModePaiement;
  factureId: number;
  clientNom: string;
  createdAt: string;
}

@Injectable({
  providedIn: 'root'
})
export class PaiementApiService {

  private url = `${environment.apiUrl}/prestataire/paiements`;

  constructor(private http: HttpClient) {}

  findAll(): Observable<PaiementResponse[]> {
    return this.http.get<PaiementResponse[]>(this.url);
  }

  findById(id: number): Observable<PaiementResponse> {
    return this.http.get<PaiementResponse>(`${this.url}/${id}`);
  }

  create(request: PaiementRequest): Observable<PaiementResponse> {
    return this.http.post<PaiementResponse>(this.url, request);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}
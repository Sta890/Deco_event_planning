import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { PrestationResponse } from './prestation-api';

export interface DemandeDevisRequest {
  nom: string;
  telephone: string;
  email: string;
  typeEvenement: string;
  dateEvenement: string;
  lieu: string;
  nombrePersonnes: number | null;
  message: string;
}

@Injectable({
  providedIn: 'root'
})
export class DemandeApiService {

  private url = `${environment.apiUrl}/client/demandes-devis`;

  constructor(private http: HttpClient) {}

  envoyer(request: DemandeDevisRequest): Observable<PrestationResponse> {
    return this.http.post<PrestationResponse>(this.url, request);
  }
}

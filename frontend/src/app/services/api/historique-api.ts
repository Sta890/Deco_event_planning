import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface HistoriqueResponse {
  id: number;
  action: string;
  dateAction: string;
  utilisateurNom: string;
}

@Injectable({
  providedIn: 'root'
})
export class HistoriqueApiService {

  private url = `${environment.apiUrl}/prestataire/historique`;

  constructor(private http: HttpClient) {}

  findAll(): Observable<HistoriqueResponse[]> {
    return this.http.get<HistoriqueResponse[]>(this.url);
  }
}
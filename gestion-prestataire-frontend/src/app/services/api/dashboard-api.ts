import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface DashboardResponse {
  totalClients: number;
  totalPrestations: number;
  devisEnAttente: number;
  facturesEnRetard: number;
  chiffreAffaires: number;
  tauxConversion: number;
}

@Injectable({
  providedIn: 'root'
})
export class DashboardApiService {

  private url = `${environment.apiUrl}/admin`;

  constructor(private http: HttpClient) {}

  getStats(): Observable<DashboardResponse> {
    return this.http.get<DashboardResponse>(`${this.url}/dashboard`);
  }
}
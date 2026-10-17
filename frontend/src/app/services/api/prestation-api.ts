import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface PrestationRequest {
  typeEvenement: string;
  dateEvenement: string;
  lieu: string;
  description?: string;
  clientId: number;
}

export interface PrestationResponse {
  id: number;
  typeEvenement: string;
  dateEvenement: string;
  lieu: string;
  description: string;
  clientId: number;
  clientNom: string;
  createdAt: string;
}

@Injectable({
  providedIn: 'root'
})
export class PrestationApiService {

  private url = `${environment.apiUrl}/prestataire/prestations`;

  constructor(private http: HttpClient) {}

  findAll(): Observable<PrestationResponse[]> {
    return this.http.get<PrestationResponse[]>(this.url);
  }

  findById(id: number): Observable<PrestationResponse> {
    return this.http.get<PrestationResponse>(`${this.url}/${id}`);
  }

  findByClientId(clientId: number): Observable<PrestationResponse[]> {
    return this.http.get<PrestationResponse[]>(`${this.url}/client/${clientId}`);
  }

  create(request: PrestationRequest): Observable<PrestationResponse> {
    return this.http.post<PrestationResponse>(this.url, request);
  }

  update(id: number, request: PrestationRequest): Observable<PrestationResponse> {
    return this.http.put<PrestationResponse>(`${this.url}/${id}`, request);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}
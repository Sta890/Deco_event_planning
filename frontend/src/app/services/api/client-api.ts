import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface ClientRequest {
  nom: string;
  telephone: string;
  adresse?: string;
  email: string;
}

export interface ClientResponse {
  id: number;
  nom: string;
  telephone: string;
  adresse: string;
  email: string;
  createdAt: string;
}

@Injectable({
  providedIn: 'root'
})
export class ClientApiService {

  private url = `${environment.apiUrl}/prestataire/clients`;

  constructor(private http: HttpClient) {}

  findAll(): Observable<ClientResponse[]> {
    return this.http.get<ClientResponse[]>(this.url);
  }

  findById(id: number): Observable<ClientResponse> {
    return this.http.get<ClientResponse>(`${this.url}/${id}`);
  }

  create(request: ClientRequest): Observable<ClientResponse> {
    return this.http.post<ClientResponse>(this.url, request);
  }

  update(id: number, request: ClientRequest): Observable<ClientResponse> {
    return this.http.put<ClientResponse>(`${this.url}/${id}`, request);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface LoginRequest {
  email: string;
  motDePasse: string;
}

export interface InscriptionRequest {
  nom: string;
  email: string;
  motDePasse: string;
  telephone: string;
  adresse?: string;
}

export interface AuthResponse {
  token: string;
  email: string;
  nom: string;
  role: 'ADMIN' | 'PRESTATAIRE' | 'CLIENT';
  id: number;
}

@Injectable({
  providedIn: 'root'
})
export class AuthApiService {

  private url = `${environment.apiUrl}/auth`;

  constructor(private http: HttpClient) {}

  login(request: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.url}/login`, request);
  }

  inscrire(request: InscriptionRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.url}/inscription`, request);
  }
}
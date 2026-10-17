import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface ArticleRequest {
  nom: string;
  description?: string;
  prixUnitaire: number;
  typeEvenement: string;
}

export interface ArticleResponse {
  id: number;
  nom: string;
  description: string;
  prixUnitaire: number;
  typeEvenement: string;
}

@Injectable({
  providedIn: 'root'
})
export class ArticleApiService {

  private url = `${environment.apiUrl}/prestataire/articles`;

  constructor(private http: HttpClient) {}

  findAll(): Observable<ArticleResponse[]> {
    return this.http.get<ArticleResponse[]>(this.url);
  }

  findById(id: number): Observable<ArticleResponse> {
    return this.http.get<ArticleResponse>(`${this.url}/${id}`);
  }

  create(request: ArticleRequest): Observable<ArticleResponse> {
    return this.http.post<ArticleResponse>(this.url, request);
  }

  update(id: number, request: ArticleRequest): Observable<ArticleResponse> {
    return this.http.put<ArticleResponse>(`${this.url}/${id}`, request);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}
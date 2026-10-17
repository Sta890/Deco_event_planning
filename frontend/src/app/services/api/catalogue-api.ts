import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ArticleResponse } from './article-api';

@Injectable({
  providedIn: 'root'
})
export class CatalogueApiService {

  private url = `${environment.apiUrl}/client`;

  constructor(private http: HttpClient) {}

  getArticles(): Observable<ArticleResponse[]> {
    return this.http.get<ArticleResponse[]>(`${this.url}/articles`);
  }

  getArticlesByTypeEvenement(typeEvenement: string): Observable<ArticleResponse[]> {
    return this.http.get<ArticleResponse[]>(`${this.url}/articles?typeEvenement=${typeEvenement}`);
  }
}
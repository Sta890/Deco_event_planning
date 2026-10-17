import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { TypeNotification } from '../../shared/enums';

export interface NotificationResponse {
  id: number;
  titre: string;
  message: string;
  type: TypeNotification;
  referenceId: number | null;
  lue: boolean;
  createdAt: string;
}

@Injectable({
  providedIn: 'root'
})
export class NotificationApiService {

  private url = `${environment.apiUrl}/client/notifications`;

  constructor(private http: HttpClient) {}

  findAll(): Observable<NotificationResponse[]> {
    return this.http.get<NotificationResponse[]>(this.url);
  }

  countNonLues(): Observable<{ count: number }> {
    return this.http.get<{ count: number }>(`${this.url}/non-lues`);
  }

  marquerCommeLue(id: number): Observable<NotificationResponse> {
    return this.http.put<NotificationResponse>(`${this.url}/${id}/lue`, null);
  }

  marquerToutesCommeLues(): Observable<{ count: number }> {
    return this.http.put<{ count: number }>(`${this.url}/tout-lire`, null);
  }
}
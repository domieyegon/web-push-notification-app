import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class MessageService {

  readonly endpoint = "http://localhost:8080/api";

  constructor(private http: HttpClient) { }

  getPublicKey(): Observable<HttpResponse<any>> {
    return this.http.get<any>(`${this.endpoint}/public-key`, {observe: 'response'})
  }

  subscribe(req: any): Observable<HttpResponse<any>> {
    return this.http.post<any>(`${this.endpoint}/subscribe`, req ,{observe: 'response'})
  }
}

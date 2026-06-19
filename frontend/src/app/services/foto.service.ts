import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface FotoMeta {
  id: number;
  nomeFile: string;
  contentType: string;
  ordine?: number;
}

@Injectable({ providedIn: 'root' })
export class FotoService {

  private apiUrl = 'http://localhost:8080/api/foto';

  constructor(private http: HttpClient) {}

  getByAnnuncio(annuncioId: number): Observable<FotoMeta[]> {
    return this.http.get<FotoMeta[]>(`${this.apiUrl}/annuncio/${annuncioId}`);
  }

  getUrlImmagine(fotoId: number): string {
    return `${this.apiUrl}/${fotoId}/raw`;
  }

  upload(annuncioId: number, file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post(`${this.apiUrl}/annuncio/${annuncioId}`, formData);
  }

  elimina(fotoId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${fotoId}`);
  }
}

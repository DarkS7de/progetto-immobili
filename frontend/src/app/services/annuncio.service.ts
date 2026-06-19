import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Annuncio } from '../models/annuncio.model';

@Injectable({ providedIn: 'root' })
export class AnnuncioService {

  private apiUrl = 'http://localhost:8080/api/annunci';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Annuncio[]> {
    return this.http.get<Annuncio[]>(this.apiUrl);
  }

  getById(id: number): Observable<Annuncio> {
    return this.http.get<Annuncio>(`${this.apiUrl}/${id}`);
  }

  getByVenditore(venditoreId: number): Observable<Annuncio[]> {
    return this.http.get<Annuncio[]>(`${this.apiUrl}/venditore/${venditoreId}`);
  }

  ricerca(filtri: {
    tipo?: string;
    categoriaId?: number;
    prezzoMin?: number;
    prezzoMax?: number;
    mqMin?: number;
    mqMax?: number;
  }): Observable<Annuncio[]> {
    const params: any = {};
    Object.entries(filtri).forEach(([key, value]) => {
      if (value !== undefined && value !== null && value !== '') {
        params[key] = value;
      }
    });
    return this.http.get<Annuncio[]>(`${this.apiUrl}/ricerca`, { params });
  }

  crea(annuncio: Partial<Annuncio>, venditoreId: number): Observable<Annuncio> {
    return this.http.post<Annuncio>(`${this.apiUrl}?venditoreId=${venditoreId}`, annuncio);
  }

  modifica(id: number, annuncio: Partial<Annuncio>, utenteId: number): Observable<Annuncio> {
    return this.http.put<Annuncio>(`${this.apiUrl}/${id}?utenteId=${utenteId}`, annuncio);
  }

  ribassaPrezzo(id: number, nuovoPrezzo: number, utenteId: number): Observable<Annuncio> {
    return this.http.patch<Annuncio>(
      `${this.apiUrl}/${id}/ribasso?utenteId=${utenteId}`,
      { nuovoPrezzo }
    );
  }

  elimina(id: number, utenteId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}?utenteId=${utenteId}`);
  }
}

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Recensione {
  id: number;
  testo: string;
  voto: number;
  data: string;
  acquirente: {
    id: number;
    nome: string;
    cognome: string;
  };
}

@Injectable({ providedIn: 'root' })
export class RecensioneService {

  private apiUrl = 'http://localhost:8080/api/recensioni';

  constructor(private http: HttpClient) {}

  getByAnnuncio(annuncioId: number): Observable<Recensione[]> {
    return this.http.get<Recensione[]>(`${this.apiUrl}/annuncio/${annuncioId}`);
  }

  crea(recensione: {
    testo: string;
    voto: number;
    acquirente: { id: number };
    annuncio: { id: number };
  }): Observable<Recensione> {
    return this.http.post<Recensione>(this.apiUrl, recensione);
  }

  elimina(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}

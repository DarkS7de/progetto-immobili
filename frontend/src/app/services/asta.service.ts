import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Asta {
  id: number;
  dataInizio: string;
  dataFine: string;
  offertaMinima: number;
  attiva: boolean;
}

export interface Offerta {
  id: number;
  importo: number;
  data: string;
  acquirente: {
    id: number;
    nome: string;
    cognome: string;
    email: string;
    telefono?: string;
  };
}

@Injectable({ providedIn: 'root' })
export class AstaService {

  private apiAste = 'http://localhost:8080/api/aste';
  private apiOfferte = 'http://localhost:8080/api/offerte';

  constructor(private http: HttpClient) {}

  getByAnnuncio(annuncioId: number): Observable<Asta> {
    return this.http.get<Asta>(`${this.apiAste}/annuncio/${annuncioId}`);
  }

  crea(annuncioId: number, asta: {
    dataInizio: string;
    dataFine: string;
    offertaMinima: number;
  }, utenteId: number): Observable<Asta> {
    return this.http.post<Asta>(`${this.apiAste}/annuncio/${annuncioId}?utenteId=${utenteId}`, asta);
  }

  chiudi(astaId: number, utenteId: number): Observable<Asta> {
    return this.http.patch<Asta>(`${this.apiAste}/${astaId}/chiudi?utenteId=${utenteId}`, {});
  }

  getOfferte(astaId: number): Observable<Offerta[]> {
    return this.http.get<Offerta[]>(`${this.apiOfferte}/asta/${astaId}`);
  }

  faiOfferta(astaId: number, importo: number, acquirenteId: number): Observable<Offerta> {
    return this.http.post<Offerta>(
      `${this.apiOfferte}/asta/${astaId}?acquirenteId=${acquirenteId}`,
      { importo }
    );
  }
}

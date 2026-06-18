import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Messaggio {
  id: number;
  oggetto: string;
  testo: string;
  nomeMittente: string;
  emailMittente: string;
  telefonoMittente?: string;
  data: string;
  letto: boolean;
}

@Injectable({ providedIn: 'root' })
export class MessaggioService {

  private apiUrl = 'http://localhost:8080/api/messaggi';

  constructor(private http: HttpClient) {}

  invia(annuncioId: number, dati: {
    oggetto: string;
    testo: string;
    nomeMittente: string;
    emailMittente: string;
    telefonoMittente?: string;
    mittenteId?: number;
  }): Observable<any> {
    return this.http.post(`${this.apiUrl}/annuncio/${annuncioId}`, dati);
  }

  ricevuti(utenteId: number): Observable<Messaggio[]> {
    return this.http.get<Messaggio[]>(`${this.apiUrl}/ricevuti/${utenteId}`);
  }

  segnaLetto(messaggioId: number): Observable<any> {
    return this.http.patch(`${this.apiUrl}/${messaggioId}/letto`, {});
  }
}

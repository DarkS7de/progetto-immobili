import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { Utente } from '../models/annuncio.model';

@Injectable({ providedIn: 'root' })
export class AuthService {

  private apiUrl = 'http://localhost:8080/api/utenti';
  private readonly STORAGE_KEY = 'immobili_utente';

  utenteCorrente = signal<Utente | null>(this.leggiDaStorage());

  constructor(private http: HttpClient) {}

  registra(datiUtente: Partial<Utente> & { password: string }): Observable<Utente> {
    return this.http.post<Utente>(`${this.apiUrl}/registrazione`, datiUtente);
  }

  login(email: string, password: string): Observable<Utente> {
    return this.http.post<Utente>(`${this.apiUrl}/login`, { email, password })
      .pipe(
        tap(utente => {
          this.salvaInStorage(utente);
          this.utenteCorrente.set(utente);
        })
      );
  }

  logout(): void {
    localStorage.removeItem(this.STORAGE_KEY);
    this.utenteCorrente.set(null);
  }

  isLoggato(): boolean {
    return this.utenteCorrente() !== null;
  }

  isAdmin(): boolean {
    return this.utenteCorrente()?.ruolo === 'ADMIN';
  }

  isVenditore(): boolean {
    return this.utenteCorrente()?.ruolo === 'VENDITORE';
  }

  isAcquirente(): boolean {
    return this.utenteCorrente()?.ruolo === 'ACQUIRENTE';
  }

  private salvaInStorage(utente: Utente): void {
    localStorage.setItem(this.STORAGE_KEY, JSON.stringify(utente));
  }

  private leggiDaStorage(): Utente | null {
    const stringa = localStorage.getItem(this.STORAGE_KEY);
    return stringa ? JSON.parse(stringa) : null;
  }

  getTuttiUtenti(): Observable<Utente[]> {
    return this.http.get<Utente[]>(`${this.apiUrl}`);
  }

  banna(utenteId: number, adminId: number): Observable<Utente> {
    return this.http.post<Utente>(`${this.apiUrl}/${utenteId}/banna?adminId=${adminId}`, {});
  }

  sbanna(utenteId: number, adminId: number): Observable<Utente> {
    return this.http.post<Utente>(`${this.apiUrl}/${utenteId}/sbanna?adminId=${adminId}`, {});
  }

  nominaAdmin(utenteId: number, adminId: number): Observable<Utente> {
    return this.http.post<Utente>(`${this.apiUrl}/${utenteId}/nomina-admin?adminId=${adminId}`, {});
  }
}

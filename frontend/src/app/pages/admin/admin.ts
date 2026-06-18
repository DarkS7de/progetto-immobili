import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { Utente } from '../../models/annuncio.model';

@Component({
  selector: 'app-admin',
  imports: [CommonModule],
  templateUrl: './admin.html',
  styleUrl: './admin.css'
})
export class Admin implements OnInit {

  authService = inject(AuthService);
  private router = inject(Router);

  utenti = signal<Utente[]>([]);
  caricamento = signal(true);
  messaggio = signal<string | null>(null);

  ngOnInit(): void {
    // Solo gli admin accedono a questa pagina
    if (!this.authService.isAdmin()) {
      this.router.navigate(['/']);
      return;
    }
    this.caricaUtenti();
  }

  caricaUtenti(): void {
    this.caricamento.set(true);
    this.authService.getTuttiUtenti().subscribe({
      next: (u) => {
        this.utenti.set(u);
        this.caricamento.set(false);
      },
      error: () => this.caricamento.set(false)
    });
  }

  banna(utente: Utente): void {
    const admin = this.authService.utenteCorrente();
    if (!admin) return;
    this.authService.banna(utente.id, admin.id).subscribe({
      next: () => { this.messaggio.set(`${utente.nome} è stato bannato.`); this.caricaUtenti(); },
      error: (err) => this.messaggio.set(err.error?.errore ?? 'Errore')
    });
  }

  sbanna(utente: Utente): void {
    const admin = this.authService.utenteCorrente();
    if (!admin) return;
    this.authService.sbanna(utente.id, admin.id).subscribe({
      next: () => { this.messaggio.set(`${utente.nome} è stato sbannato.`); this.caricaUtenti(); },
      error: (err) => this.messaggio.set(err.error?.errore ?? 'Errore')
    });
  }

  nominaAdmin(utente: Utente): void {
    if (!confirm(`Vuoi nominare ${utente.nome} ${utente.cognome} come amministratore?`)) return;
    const admin = this.authService.utenteCorrente();
    if (!admin) return;
    this.authService.nominaAdmin(utente.id, admin.id).subscribe({
      next: () => { this.messaggio.set(`${utente.nome} è ora amministratore.`); this.caricaUtenti(); },
      error: (err) => this.messaggio.set(err.error?.errore ?? 'Errore')
    });
  }
}

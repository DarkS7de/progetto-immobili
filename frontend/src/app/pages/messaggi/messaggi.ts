import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MessaggioService, Messaggio } from '../../services/messaggio.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-messaggi',
  imports: [CommonModule],
  templateUrl: './messaggi.html',
  styleUrl: './messaggi.css'
})
export class Messaggi implements OnInit {

  private messaggioService = inject(MessaggioService);
  private authService = inject(AuthService);
  private router = inject(Router);

  messaggi = signal<Messaggio[]>([]);
  caricamento = signal(true);

  ngOnInit(): void {
    const utente = this.authService.utenteCorrente();
    if (!utente || utente.ruolo === 'ACQUIRENTE') {
      this.router.navigate(['/']);
      return;
    }

    this.messaggioService.ricevuti(utente.id).subscribe({
      next: (msg) => {
        this.messaggi.set(msg);
        this.caricamento.set(false);
      },
      error: () => this.caricamento.set(false)
    });
  }

  apri(messaggio: Messaggio): void {
    if (!messaggio.letto) {
      this.messaggioService.segnaLetto(messaggio.id).subscribe(() => {
        // Aggiorno lo stato locale
        const aggiornati = this.messaggi().map(m =>
          m.id === messaggio.id ? { ...m, letto: true } : m
        );
        this.messaggi.set(aggiornati);
      });
    }
  }
}

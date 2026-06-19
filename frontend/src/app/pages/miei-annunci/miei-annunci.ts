import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { AnnuncioService } from '../../services/annuncio.service';
import { AuthService } from '../../services/auth.service';
import { Annuncio } from '../../models/annuncio.model';
import { FotoService } from '../../services/foto.service';
import { AstaService } from '../../services/asta.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-miei-annunci',
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './miei-annunci.html',
  styleUrl: './miei-annunci.css'
})
export class MieiAnnunci implements OnInit {

  private annuncioService = inject(AnnuncioService);
  private authService = inject(AuthService);
  private router = inject(Router);
  private fotoService = inject(FotoService);
  private astaService = inject(AstaService);

  annunci = signal<Annuncio[]>([]);
  caricamento = signal(true);
  errore = signal<string | null>(null);
  // Modale crea asta
  mostraModalAsta = signal(false);
  annuncioPerAsta: Annuncio | null = null;
  formAsta = {
    dataInizio: '',
    dataFine: '',
    offertaMinima: 0
  };
  erroreAsta = signal<string | null>(null);

  apriModalAsta(annuncio: Annuncio): void {
    this.annuncioPerAsta = annuncio;
    this.formAsta = { dataInizio: '', dataFine: '', offertaMinima: annuncio.prezzo };
    this.erroreAsta.set(null);
    this.mostraModalAsta.set(true);
  }

  chiudiModalAsta(): void {
    this.mostraModalAsta.set(false);
  }

  creaAsta(): void {
    const utente = this.authService.utenteCorrente();
    if (!utente || !this.annuncioPerAsta) return;

    this.astaService.crea(this.annuncioPerAsta.id, {
      dataInizio: this.formAsta.dataInizio,
      dataFine: this.formAsta.dataFine,
      offertaMinima: this.formAsta.offertaMinima
    }, utente.id).subscribe({
      next: () => {
        this.mostraModalAsta.set(false);
        alert('Asta creata con successo!');
      },
      error: (err) => {
        this.erroreAsta.set(err.error?.errore ?? 'Errore durante la creazione dell\'asta');
      }
    });
  }

  ngOnInit(): void {
    const utente = this.authService.utenteCorrente();
    if (!utente || utente.ruolo === 'ACQUIRENTE') {
      this.router.navigate(['/']);
      return;
    }

    this.caricaAnnunci();
  }

  caricaAnnunci(): void {
    const utente = this.authService.utenteCorrente();
    if (!utente) return;

    this.caricamento.set(true);
    this.annuncioService.getByVenditore(utente.id).subscribe({
      next: (dati) => {
        this.annunci.set(dati);
        this.caricamento.set(false);
      },
      error: () => {
        this.errore.set('Impossibile caricare i tuoi annunci.');
        this.caricamento.set(false);
      }
    });
  }

  elimina(annuncio: Annuncio): void {
    if (!confirm(`Sei sicuro di voler eliminare "${annuncio.titolo}"?`)) {
      return;
    }
    const utente = this.authService.utenteCorrente();
    if (!utente) return;

    this.annuncioService.elimina(annuncio.id, utente.id).subscribe({
      next: () => {
        this.caricaAnnunci();   // ricarica la lista
      },
      error: (err) => {
        alert(err.error?.errore ?? 'Errore durante l\'eliminazione');
      }
    });
  }

  caricaFoto(annuncio: Annuncio, event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files || input.files.length === 0) return;

    const file = input.files[0];
    this.fotoService.upload(annuncio.id, file).subscribe({
      next: () => {
        alert('Foto caricata con successo!');
        input.value = '';  // resetta l'input
      },
      error: (err) => {
        alert(err.error?.errore ?? 'Errore durante il caricamento della foto');
      }
    });
  }

  ribassa(annuncio: Annuncio): void {
    const inputStr = prompt(
      `Prezzo attuale: € ${annuncio.prezzo}\nInserisci il NUOVO prezzo (deve essere minore):`,
      ''
    );
    if (!inputStr) return;

    const nuovoPrezzo = Number(inputStr);
    if (isNaN(nuovoPrezzo) || nuovoPrezzo <= 0) {
      alert('Prezzo non valido');
      return;
    }

    const utente = this.authService.utenteCorrente();
    if (!utente) return;

    this.annuncioService.ribassaPrezzo(annuncio.id, nuovoPrezzo, utente.id).subscribe({
      next: () => {
        this.caricaAnnunci();
      },
      error: (err) => {
        alert(err.error?.errore ?? 'Errore durante il ribasso');
      }
    });
  }
}

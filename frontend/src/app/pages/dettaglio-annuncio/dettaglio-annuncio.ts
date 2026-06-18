import { Component, OnInit, OnDestroy, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { AnnuncioService } from '../../services/annuncio.service';
import { FotoService, FotoMeta } from '../../services/foto.service';
import { AuthService } from '../../services/auth.service';
import { Annuncio } from '../../models/annuncio.model';
import * as L from 'leaflet';
import { MessaggioService } from '../../services/messaggio.service';
import { FormsModule } from '@angular/forms';
import { RecensioneService, Recensione } from '../../services/recensione.service';
import { AstaService, Asta, Offerta } from '../../services/asta.service';

@Component({
  selector: 'app-dettaglio-annuncio',
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './dettaglio-annuncio.html',
  styleUrl: './dettaglio-annuncio.css'
})
export class DettaglioAnnuncio implements OnInit, OnDestroy {

  private route = inject(ActivatedRoute);
  private annuncioService = inject(AnnuncioService);
  private fotoService = inject(FotoService);
  // tolto private perchè angular può accedere solo a campi pubblici
  authService = inject(AuthService);
  private messaggioService = inject(MessaggioService);
  private recensioneService = inject(RecensioneService);
  private astaService = inject(AstaService);

  annuncio = signal<Annuncio | null>(null);
  foto = signal<FotoMeta[]>([]);
  fotoSelezionata = signal<number>(0);
  caricamento = signal(true);
  errore = signal<string | null>(null);
  // Form di contatto
  mostraFormContatto = signal(false);
  invioInCorso = signal(false);
  messaggioInviato = signal(false);
  // Recensioni
  recensioni = signal<Recensione[]>([]);
  formRecensione = { testo: '', voto: 5 };
  invioRecensioneInCorso = signal(false);
  erroreRecensione = signal<string | null>(null);
  // Asta
  asta = signal<Asta | null>(null);
  offerte = signal<Offerta[]>([]);
  importoOfferta: number | null = null;
  offertaInCorso = signal(false);
  erroreOfferta = signal<string | null>(null);

  formContatto = {
    oggetto: '',
    testo: '',
    nomeMittente: '',
    emailMittente: '',
    telefonoMittente: ''
  };

  private mappa: L.Map | null = null;

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));

    this.annuncioService.getById(id).subscribe({
      next: (dati) => {
        this.annuncio.set(dati);
        this.caricamento.set(false);
        setTimeout(() => this.inizializzaMappa(), 0);
        this.fotoService.getByAnnuncio(id).subscribe(f => {
          this.foto.set(f);
          if (f.length > 0) {
            this.fotoSelezionata.set(f[0].id);
            this.caricaRecensioni(id);
            this.caricaAsta(id);
          }
        });
      },
      error: (err) => {
        this.errore.set('Annuncio non trovato.');
        this.caricamento.set(false);
        console.error(err);
      }
    });
  }

  // È il proprietario dell'annuncio (o admin) → può vedere i contatti degli offerenti
  isProprietarioOAdmin(): boolean {
    const utente = this.authService.utenteCorrente();
    const a = this.annuncio();
    if (!utente || !a) return false;
    return utente.ruolo === 'ADMIN' || utente.id === a.venditore.id;
  }

  caricaAsta(annuncioId: number): void {
    this.astaService.getByAnnuncio(annuncioId).subscribe({
      next: (a) => {
        this.asta.set(a);
        this.caricaOfferte(a.id);
      },
      error: () => {
        // 404 = nessuna asta per questo annuncio, è normale
        this.asta.set(null);
      }
    });
  }

  chiudiAsta(): void {
    const utente = this.authService.utenteCorrente();
    const a = this.asta();
    if (!utente || !a) return;

    if (!confirm('Vuoi chiudere questa asta? Non si potranno più fare offerte.')) {
      return;
    }

    this.astaService.chiudi(a.id, utente.id).subscribe({
      next: (astaAggiornata) => {
        this.asta.set(astaAggiornata);  // aggiorna lo stato (attiva = false)
      },
      error: (err) => {
        alert(err.error?.errore ?? 'Errore durante la chiusura dell\'asta');
      }
    });
  }

  caricaOfferte(astaId: number): void {
    this.astaService.getOfferte(astaId).subscribe(o => this.offerte.set(o));
  }

  // Offerta più alta attuale
  offertaTop(): number {
    const o = this.offerte();
    return o.length > 0 ? o[0].importo : 0;
  }

  // Può offrire: acquirente loggato, asta attiva e nel periodo valido
  puoOffrire(): boolean {
    const utente = this.authService.utenteCorrente();
    const a = this.asta();
    if (!utente || utente.ruolo !== 'ACQUIRENTE' || !a || !a.attiva) return false;
    const ora = new Date();
    return ora >= new Date(a.dataInizio) && ora <= new Date(a.dataFine);
  }

  faiOfferta(): void {
    const utente = this.authService.utenteCorrente();
    const a = this.asta();
    if (!utente || !a || this.importoOfferta == null) return;

    this.offertaInCorso.set(true);
    this.erroreOfferta.set(null);

    this.astaService.faiOfferta(a.id, this.importoOfferta, utente.id).subscribe({
      next: () => {
        this.offertaInCorso.set(false);
        this.importoOfferta = null;
        this.caricaOfferte(a.id);  // ricarico la classifica
      },
      error: (err) => {
        this.offertaInCorso.set(false);
        this.erroreOfferta.set(err.error?.errore ?? 'Errore durante l\'offerta');
      }
    });
  }

  caricaRecensioni(annuncioId: number): void {
    this.recensioneService.getByAnnuncio(annuncioId).subscribe(r => this.recensioni.set(r));
  }

  // Può recensire: utente loggato, ruolo ACQUIRENTE, che non ha già recensito
  puoRecensire(): boolean {
    const utente = this.authService.utenteCorrente();
    if (!utente || utente.ruolo !== 'ACQUIRENTE') return false;
    // Controllo che non abbia già recensito questo annuncio
    const giaRecensito = this.recensioni().some(r => r.acquirente.id === utente.id);
    return !giaRecensito;
  }

  // Messaggio esplicativo del perché non può recensire
  motivoNoRecensione(): string {
    const utente = this.authService.utenteCorrente();
    if (!utente) return 'Accedi come acquirente per lasciare una recensione.';
    if (utente.ruolo !== 'ACQUIRENTE') return 'Solo gli acquirenti possono lasciare recensioni.';
    return 'Hai già recensito questo immobile.';
  }

  inviaRecensione(): void {
    const utente = this.authService.utenteCorrente();
    const a = this.annuncio();
    if (!utente || !a) return;

    this.invioRecensioneInCorso.set(true);
    this.erroreRecensione.set(null);

    this.recensioneService.crea({
      testo: this.formRecensione.testo,
      voto: this.formRecensione.voto,
      acquirente: { id: utente.id },
      annuncio: { id: a.id }
    }).subscribe({
      next: () => {
        this.invioRecensioneInCorso.set(false);
        this.formRecensione = { testo: '', voto: 5 };
        this.caricaRecensioni(a.id);   // ricarico la lista aggiornata
      },
      error: (err) => {
        this.invioRecensioneInCorso.set(false);
        this.erroreRecensione.set(err.error?.errore ?? 'Errore durante l\'invio della recensione');
      }
    });
  }

  // Media dei voti (per il riepilogo)
  mediaVoti(): number {
    const recs = this.recensioni();
    if (recs.length === 0) return 0;
    const somma = recs.reduce((acc, r) => acc + r.voto, 0);
    return Math.round((somma / recs.length) * 10) / 10;
  }

  // Helper per generare un array da usare con *ngFor per le stelline
  stelle(n: number): number[] {
    return Array(n).fill(0);
  }

  ngOnDestroy(): void {
    if (this.mappa) {
      this.mappa.remove();
    }
  }

  urlFoto(fotoId: number): string {
    return this.fotoService.getUrlImmagine(fotoId);
  }

  selezionaFoto(fotoId: number): void {
    this.fotoSelezionata.set(fotoId);
  }

  // Può gestire le foto solo il proprietario dell'annuncio o un admin
  puoGestireFoto(): boolean {
    const utente = this.authService.utenteCorrente();
    const a = this.annuncio();
    if (!utente || !a) return false;
    return utente.ruolo === 'ADMIN' || utente.id === a.venditore.id;
  }

  eliminaFoto(fotoId: number): void {
    if (!confirm('Vuoi eliminare questa foto?')) return;

    this.fotoService.elimina(fotoId).subscribe({
      next: () => {
        const aggiornate = this.foto().filter(f => f.id !== fotoId);
        this.foto.set(aggiornate);
        if (this.fotoSelezionata() === fotoId && aggiornate.length > 0) {
          this.fotoSelezionata.set(aggiornate[0].id);
        }
      },
      error: () => alert('Errore durante l\'eliminazione della foto')
    });
  }

  private inizializzaMappa(): void {
    const a = this.annuncio();
    if (!a) return;

    this.mappa = L.map('mappa-immobile').setView([a.latitudine, a.longitudine], 15);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '© OpenStreetMap',
      maxZoom: 19
    }).addTo(this.mappa);

    const iconaCustom = L.icon({
      iconUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png',
      iconRetinaUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png',
      shadowUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png',
      iconSize: [25, 41],
      iconAnchor: [12, 41],
      popupAnchor: [1, -34],
      shadowSize: [41, 41]
    });

    L.marker([a.latitudine, a.longitudine], { icon: iconaCustom })
      .addTo(this.mappa)
      .bindPopup(`<strong>${a.titolo}</strong><br>${a.indirizzo ?? ''}`)
      .openPopup();
  }

  contattaVenditore(): void {
    const a = this.annuncio();
    if (!a) return;

    // Oggetto precompilato con codice + titolo, come richiede la traccia
    this.formContatto.oggetto = `${a.codice} - ${a.titolo}`;

    // Se l'utente è loggato, precompilo i suoi dati
    const utente = this.authService.utenteCorrente();
    if (utente) {
      this.formContatto.nomeMittente = `${utente.nome} ${utente.cognome}`;
      this.formContatto.emailMittente = utente.email;
      this.formContatto.telefonoMittente = utente.telefono ?? '';
    }

    this.messaggioInviato.set(false);
    this.mostraFormContatto.set(true);
  }

  chiudiFormContatto(): void {
    this.mostraFormContatto.set(false);
  }

  inviaMessaggio(): void {
    const a = this.annuncio();
    if (!a) return;

    this.invioInCorso.set(true);
    const utente = this.authService.utenteCorrente();

    this.messaggioService.invia(a.id, {
      oggetto: this.formContatto.oggetto,
      testo: this.formContatto.testo,
      nomeMittente: this.formContatto.nomeMittente,
      emailMittente: this.formContatto.emailMittente,
      telefonoMittente: this.formContatto.telefonoMittente,
      mittenteId: utente?.id
    }).subscribe({
      next: () => {
        this.invioInCorso.set(false);
        this.messaggioInviato.set(true);
        // Pulisco il testo (lascio i dati anagrafici)
        this.formContatto.testo = '';
      },
      error: () => {
        this.invioInCorso.set(false);
        alert('Errore durante l\'invio del messaggio');
      }
    });
  }

  promuoviSuFacebook(): void {
    const a = this.annuncio();
    if (!a) return;

    // URL dell'annuncio (in produzione sarebbe il dominio pubblico,
    // in locale usiamo l'indirizzo localhost)
    const urlAnnuncio = `${window.location.origin}/annunci/${a.id}`;

    // Testo precompilato per il post
    const testo = `${a.titolo} - € ${a.prezzo} - ${a.metriQuadri} m² a ${a.indirizzo ?? ''}`;

    // Share Dialog di Facebook (sharer.php): apre Facebook con il link da condividere
    const urlFacebook = `https://www.facebook.com/sharer/sharer.php?u=${encodeURIComponent(urlAnnuncio)}&quote=${encodeURIComponent(testo)}`;

    // Apre in una finestra popup
    window.open(urlFacebook, '_blank', 'width=600,height=500');
  }
}

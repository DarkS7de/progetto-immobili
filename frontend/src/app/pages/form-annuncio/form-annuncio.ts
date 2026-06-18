import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { AnnuncioService } from '../../services/annuncio.service';
import { CategoriaService } from '../../services/categoria.service';
import { AuthService } from '../../services/auth.service';
import { Categoria } from '../../models/annuncio.model';

@Component({
  selector: 'app-form-annuncio',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './form-annuncio.html',
  styleUrl: './form-annuncio.css'
})
export class FormAnnuncio implements OnInit {

  private annuncioService = inject(AnnuncioService);
  private categoriaService = inject(CategoriaService);
  private authService = inject(AuthService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  modalitaModifica = signal(false);
  annuncioId: number | null = null;
  categorie = signal<Categoria[]>([]);
  errore = signal<string | null>(null);
  inCorso = signal(false);

  form = {
    titolo: '',
    descrizione: '',
    prezzo: 0,
    metriQuadri: 0,
    latitudine: 37.5079,    // default: Catania
    longitudine: 15.0830,
    indirizzo: '',
    tipoTransazione: 'VENDITA' as 'VENDITA' | 'AFFITTO',
    categoriaId: null as number | null
  };

  ngOnInit(): void {
    // Controllo permessi
    const utente = this.authService.utenteCorrente();
    if (!utente || utente.ruolo === 'ACQUIRENTE') {
      this.router.navigate(['/']);
      return;
    }

    // Carico le categorie per il dropdown
    this.categoriaService.getAll().subscribe(cats => this.categorie.set(cats));

    // Controllo se siamo in modalità modifica
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.modalitaModifica.set(true);
      this.annuncioId = Number(idParam);
      this.caricaAnnuncio(this.annuncioId);
    }
  }

  caricaAnnuncio(id: number): void {
    this.annuncioService.getById(id).subscribe({
      next: (a) => {
        this.form = {
          titolo: a.titolo,
          descrizione: a.descrizione,
          prezzo: a.prezzo,
          metriQuadri: a.metriQuadri,
          latitudine: a.latitudine,
          longitudine: a.longitudine,
          indirizzo: a.indirizzo ?? '',
          tipoTransazione: a.tipoTransazione,
          categoriaId: a.categoria.id
        };
      },
      error: () => this.errore.set('Annuncio non trovato')
    });
  }

  invia(): void {
    const utente = this.authService.utenteCorrente();
    if (!utente || !this.form.categoriaId) return;

    this.inCorso.set(true);
    this.errore.set(null);

    const payload = {
      titolo: this.form.titolo,
      descrizione: this.form.descrizione,
      prezzo: this.form.prezzo,
      metriQuadri: this.form.metriQuadri,
      latitudine: this.form.latitudine,
      longitudine: this.form.longitudine,
      indirizzo: this.form.indirizzo,
      tipoTransazione: this.form.tipoTransazione,
      categoria: { id: this.form.categoriaId }
    };

    const obs$ = this.modalitaModifica()
      ? this.annuncioService.modifica(this.annuncioId!, payload as any, utente.id)
      : this.annuncioService.crea(payload as any, utente.id);

    obs$.subscribe({
      next: () => {
        this.inCorso.set(false);
        this.router.navigate(['/venditore/miei-annunci']);
      },
      error: (err) => {
        this.inCorso.set(false);
        this.errore.set(err.error?.errore ?? 'Errore durante il salvataggio');
      }
    });
  }
}

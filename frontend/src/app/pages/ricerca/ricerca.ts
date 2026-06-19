import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AnnuncioService } from '../../services/annuncio.service';
import { CategoriaService } from '../../services/categoria.service';
import { Annuncio, Categoria } from '../../models/annuncio.model';

@Component({
  selector: 'app-ricerca',
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './ricerca.html',
  styleUrl: './ricerca.css'
})
export class Ricerca implements OnInit {

  private annuncioService = inject(AnnuncioService);
  private categoriaService = inject(CategoriaService);
  private route = inject(ActivatedRoute);

  risultati = signal<Annuncio[]>([]);
  categorie = signal<Categoria[]>([]);
  caricamento = signal(false);
  ricercaEffettuata = signal(false);

  filtri = {
    testo: '',
    localita: '',
    tipo: '',
    categoriaId: null as number | null,
    prezzoMin: null as number | null,
    prezzoMax: null as number | null,
    mqMin: null as number | null,
    mqMax: null as number | null
  };

  ordinaPer = 'recenti';
  ordineCrescente = true;

  ngOnInit(): void {
    this.categoriaService.getAll().subscribe(c => this.categorie.set(c));

    this.route.queryParams.subscribe(params => {
      if (params['testo']) this.filtri.testo = params['testo'];
      if (params['categoriaId']) this.filtri.categoriaId = +params['categoriaId'];
      if (params['testo'] || params['categoriaId']) {
        this.cerca();
      }
    });
  }

  cerca(): void {
    this.caricamento.set(true);
    this.ricercaEffettuata.set(true);

    this.annuncioService.ricerca({
      tipo: this.filtri.tipo || undefined,
      categoriaId: this.filtri.categoriaId ?? undefined,
      prezzoMin: this.filtri.prezzoMin ?? undefined,
      prezzoMax: this.filtri.prezzoMax ?? undefined,
      mqMin: this.filtri.mqMin ?? undefined,
      mqMax: this.filtri.mqMax ?? undefined
    }).subscribe({
      next: (dati) => {
        let lista = dati;
        if (this.filtri.testo) {
          const t = this.filtri.testo.toLowerCase();
          lista = lista.filter(a =>
            a.titolo.toLowerCase().includes(t) ||
            a.descrizione.toLowerCase().includes(t) ||
            (a.indirizzo?.toLowerCase().includes(t) ?? false)
          );
        }

        if (this.filtri.localita) {
          const loc = this.filtri.localita.toLowerCase();
          lista = lista.filter(a => a.indirizzo?.toLowerCase().includes(loc) ?? false);
        }

        this.risultati.set(this.applicaOrdinamento(lista));
        this.caricamento.set(false);
      },
      error: () => this.caricamento.set(false)
    });
  }

  cambiaOrdinamento(): void {
    this.risultati.set(this.applicaOrdinamento(this.risultati()));
  }

  private applicaOrdinamento(lista: Annuncio[]): Annuncio[] {
    const ord = [...lista];
    const dir = this.ordineCrescente ? 1 : -1;
    switch (this.ordinaPer) {
      case 'prezzo':
        return ord.sort((a, b) => (a.prezzo - b.prezzo) * dir);
      case 'mq':
        return ord.sort((a, b) => (a.metriQuadri - b.metriQuadri) * dir);
      default:
        return ord;
    }
  }

  resetFiltri(): void {
    this.filtri = { testo: '', localita: '', tipo: '', categoriaId: null, prezzoMin: null, prezzoMax: null, mqMin: null, mqMax: null };
    this.cerca();
  }
}

import { Component, OnInit, signal, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import {
  LucideSearch, LucideBuilding2, LucideTreePine, LucideCar,
  LucideHouse, LucideStore, LucideBed, LucideMapPin
} from '@lucide/angular';
import { AnnuncioService } from '../../services/annuncio.service';
import { CategoriaService } from '../../services/categoria.service';
import { Annuncio, Categoria } from '../../models/annuncio.model';

@Component({
  selector: 'app-home',
  imports: [
    CommonModule, RouterLink, FormsModule,
    LucideSearch, LucideBuilding2, LucideTreePine, LucideCar,
    LucideHouse, LucideStore, LucideBed, LucideMapPin
  ],
  templateUrl: './home.html',
  styleUrl: './home.css'
})
export class Home implements OnInit {

  private annuncioService = inject(AnnuncioService);
  private categoriaService = inject(CategoriaService);
  private router = inject(Router);

  annunci = signal<Annuncio[]>([]);
  categorie = signal<Categoria[]>([]);
  caricamento = signal(true);
  errore = signal<string | null>(null);

  testoRicerca = '';

  numAnnunci = computed(() => this.annunci().length);
  numVendita = computed(() => this.annunci().filter(a => a.tipoTransazione === 'VENDITA').length);
  numAffitto = computed(() => this.annunci().filter(a => a.tipoTransazione === 'AFFITTO').length);
  numCategorie = computed(() => this.categorie().length);

  ngOnInit(): void {
    this.annuncioService.getAll().subscribe({
      next: (dati) => {
        this.annunci.set(dati);
        this.caricamento.set(false);
      },
      error: (err) => {
        this.errore.set('Impossibile caricare gli annunci. Backend in esecuzione?');
        this.caricamento.set(false);
        console.error(err);
      }
    });

    this.categoriaService.getAll().subscribe(c => this.categorie.set(c));
  }

  cerca(): void {
    if (this.testoRicerca.trim()) {
      this.router.navigate(['/ricerca'], { queryParams: { testo: this.testoRicerca.trim() } });
    } else {
      this.router.navigate(['/ricerca']);
    }
  }

  vaiACategoria(categoriaId: number): void {
    this.router.navigate(['/ricerca'], { queryParams: { categoriaId } });
  }

  iconaCategoria(nome: string): string {
    const n = nome.toLowerCase();
    if (n.includes('appartamento')) return 'building2';
    if (n.includes('villa')) return 'house';
    if (n.includes('box')) return 'car';
    if (n.includes('terreno')) return 'tree-pine';
    if (n.includes('commerciale') || n.includes('negozio')) return 'store';
    if (n.includes('monolocale')) return 'bed';
    return 'building2';
  }
}

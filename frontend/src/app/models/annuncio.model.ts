export interface Categoria {
  id: number;
  nome: string;
  descrizione?: string;
}

export interface Utente {
  id: number;
  email: string;
  nome: string;
  cognome: string;
  telefono?: string;
  ruolo: 'ADMIN' | 'VENDITORE' | 'ACQUIRENTE';
  bannato: boolean;
  dataRegistrazione: string;
}

export interface Annuncio {
  id: number;
  codice: string;
  titolo: string;
  descrizione: string;
  prezzo: number;
  prezzoVecchio?: number | null;
  metriQuadri: number;
  latitudine: number;
  longitudine: number;
  indirizzo?: string;
  tipoTransazione: 'VENDITA' | 'AFFITTO';
  dataPubblicazione: string;
  attivo: boolean;
  venditore: Utente;
  categoria: Categoria;
  foto: any[];
}

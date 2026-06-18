import { Routes } from '@angular/router';
import { Home } from './pages/home/home';
import { DettaglioAnnuncio } from './pages/dettaglio-annuncio/dettaglio-annuncio';
import { Login } from './pages/login/login';
import { Registrazione } from './pages/registrazione/registrazione';
import { MieiAnnunci } from './pages/miei-annunci/miei-annunci';
import { FormAnnuncio } from './pages/form-annuncio/form-annuncio';
import { Ricerca } from './pages/ricerca/ricerca';
import { Messaggi } from './pages/messaggi/messaggi';
import { Admin } from './pages/admin/admin';

export const routes: Routes = [
  { path: '', component: Home },
  { path: 'annunci/:id', component: DettaglioAnnuncio },
  { path: 'login', component: Login },
  { path: 'registrazione', component: Registrazione },
  { path: 'venditore/miei-annunci', component: MieiAnnunci },
  { path: 'venditore/nuovo-annuncio', component: FormAnnuncio },
  { path: 'venditore/modifica/:id', component: FormAnnuncio },
  { path: 'venditore/messaggi', component: Messaggi },
  { path: 'ricerca', component: Ricerca },
  { path: 'admin', component: Admin },
  //questa deve restare l'ultima riga
  { path: '**', redirectTo: '' }
];

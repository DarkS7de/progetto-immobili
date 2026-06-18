import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Categoria } from '../models/annuncio.model';

@Injectable({ providedIn: 'root' })
export class CategoriaService {

  private apiUrl = 'http://localhost:8080/api/categorie';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Categoria[]> {
    return this.http.get<Categoria[]>(this.apiUrl);
  }
}

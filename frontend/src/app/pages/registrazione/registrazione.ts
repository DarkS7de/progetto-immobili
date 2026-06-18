import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { LucideHouse, LucideBriefcase, LucideZap, LucideMessageCircle, LucideTrendingUp } from '@lucide/angular';

@Component({
  selector: 'app-registrazione',
  imports: [CommonModule, RouterLink, FormsModule, LucideHouse, LucideBriefcase, LucideZap, LucideMessageCircle, LucideTrendingUp],
  templateUrl: './registrazione.html',
  styleUrl: './registrazione.css'
})
export class Registrazione {

  // Modello del form (campi inizialmente vuoti)
  form = {
    email: '',
    password: '',
    nome: '',
    cognome: '',
    telefono: '',
    ruolo: 'ACQUIRENTE' as 'VENDITORE' | 'ACQUIRENTE'
  };

  errore = signal<string | null>(null);
  inCorso = signal(false);

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  invia(): void {
    this.errore.set(null);
    this.inCorso.set(true);

    this.authService.registra(this.form).subscribe({
      next: () => {
        // Dopo la registrazione faccio login automatico
        this.authService.login(this.form.email, this.form.password).subscribe({
          next: () => {
            this.inCorso.set(false);
            this.router.navigate(['/']);
          },
          error: () => {
            this.inCorso.set(false);
            // Se per qualche motivo il login fallisce, mando alla pagina di login
            this.router.navigate(['/login']);
          }
        });
      },
      error: (err) => {
        this.inCorso.set(false);
        this.errore.set(err.error?.errore ?? 'Errore durante la registrazione');
      }
    });
  }
}

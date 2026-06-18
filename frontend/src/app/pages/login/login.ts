import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { LucideHouse, LucideSearch, LucideMapPin, LucideGavel } from '@lucide/angular';

@Component({
  selector: 'app-login',
  imports: [CommonModule, RouterLink, FormsModule, LucideHouse, LucideSearch, LucideMapPin, LucideGavel],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login {

  email = '';
  password = '';
  errore = signal<string | null>(null);
  inCorso = signal(false);

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  invia(): void {
    this.errore.set(null);
    this.inCorso.set(true);

    this.authService.login(this.email, this.password).subscribe({
      next: () => {
        this.inCorso.set(false);
        this.router.navigate(['/']);
      },
      error: (err) => {
        this.inCorso.set(false);
        this.errore.set(err.error?.errore ?? 'Credenziali errate');
      }
    });
  }
}

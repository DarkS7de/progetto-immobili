import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavigationEnd, Router, RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { LucideHouse } from '@lucide/angular';
import { AuthService } from './services/auth.service';
import { filter } from 'rxjs';

@Component({
  selector: 'app-root',
  imports: [
    CommonModule,
    RouterOutlet,
    RouterLink,
    RouterLinkActive,
    LucideHouse
  ],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  authService = inject(AuthService);
  private router = inject(Router);

  private hideNavbarRoutes = ['/login', '/registrazione'];
  mostraNavbar = signal(true);

  constructor() {
    this.router.events
      .pipe(filter(e => e instanceof NavigationEnd))
      .subscribe((event: any) => {
        this.mostraNavbar.set(!this.hideNavbarRoutes.includes(event.urlAfterRedirects));
      });
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/']);
  }
}

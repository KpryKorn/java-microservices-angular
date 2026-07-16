import { HttpClient } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { Observable, catchError, of, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { CurrentUser } from '../models/user.model';

/**
 * Gère l'état d'authentification côté frontend.
 *
 * Pattern BFF : aucun token n'est manipulé en JS. La session est un cookie HttpOnly
 * porté par l'api-gateway ; ce service se contente d'interroger `/api/me` pour savoir
 * si une session valide existe.
 */
@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);

  private readonly currentUserSignal = signal<CurrentUser | null>(null);

  readonly currentUser = this.currentUserSignal.asReadonly();
  readonly isAuthenticated = computed(() => this.currentUserSignal() !== null);

  /** Interroge le gateway pour vérifier si une session valide existe. */
  checkSession(): Observable<CurrentUser | null> {
    return this.http
      .get<CurrentUser>(`${environment.gatewayUrl}/api/me`, { withCredentials: true })
      .pipe(
        tap((user) => this.currentUserSignal.set(user)),
        catchError(() => {
          this.currentUserSignal.set(null);
          return of(null);
        }),
      );
  }

  /** Démarre le flux de connexion OAuth2 porté par l'api-gateway (Keycloak). */
  login(): void {
    window.location.href = `${environment.gatewayUrl}/oauth2/authorization/keycloak`;
  }
}

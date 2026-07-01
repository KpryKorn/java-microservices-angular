import { HttpClient } from '@angular/common/http';
import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { environment } from '../environments/environment';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.component.html',
})
export class App implements OnInit {
  private http = inject(HttpClient);

  protected readonly title = signal('frontend-app');

  protected chatGWIStatus = signal('loading..');
  protected isAuthenticated = signal(false);

  ngOnInit(): void {
    this.http
      .get(`${environment.gatewayUrl}/api/chat/ping`, {
        responseType: 'text',
        withCredentials: true,
      })
      .subscribe({
        next: (res) => {
          this.chatGWIStatus.set(res);
          this.isAuthenticated.set(true);
        },
        error: (err) => {
          console.error('Erreur authentification BFF:', err);
          this.chatGWIStatus.set('Non authentifié (401)');
          this.isAuthenticated.set(false);
        },
      });
  }

  login(): void {
    window.location.href = `${environment.gatewayUrl}/oauth2/authorization/keycloak`;
  }
}

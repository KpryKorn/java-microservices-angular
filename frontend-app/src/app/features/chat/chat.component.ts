import { HttpClient } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { environment } from '../../../environments/environment';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-chat',
  imports: [],
  templateUrl: './chat.component.html',
})
export class ChatComponent implements OnInit {
  private readonly http = inject(HttpClient);
  protected readonly authService = inject(AuthService);

  protected readonly chatStatus = signal('chargement...');

  ngOnInit(): void {
    this.http
      .get(`${environment.gatewayUrl}/api/chat/ping`, {
        responseType: 'text',
        withCredentials: true,
      })
      .subscribe({
        next: (res) => this.chatStatus.set(res),
        error: () => this.chatStatus.set('indisponible'),
      });
  }
}

import { HttpClient } from '@angular/common/http';
import { Component, OnDestroy, OnInit, computed, inject, signal } from '@angular/core';
import { environment } from '../../../environments/environment';
import { AuthService } from '../../core/services/auth.service';
import { ChatSessionService, ChatTicketMessage } from './chat-session.service';

@Component({
  selector: 'app-chat',
  imports: [],
  templateUrl: './chat.component.html',
})
export class ChatComponent implements OnInit, OnDestroy {
  private readonly http = inject(HttpClient);
  private readonly chatSessionService = inject(ChatSessionService);
  protected readonly authService = inject(AuthService);

  protected readonly chatStatus = signal('chargement...');
  protected readonly connectionStatus = signal<
    'déconnecté' | 'connexion...' | 'connecté' | 'erreur websocket'
  >('déconnecté');
  protected readonly ticketId = signal('55555555-5555-5555-5555-555555555555');
  protected readonly senderId = signal('');
  protected readonly draftMessage = signal('');
  protected readonly messages = signal<ChatTicketMessage[]>([]);
  protected readonly canSend = computed(
    () =>
      this.connectionStatus() === 'connecté' &&
      this.senderId().trim().length > 0 &&
      this.draftMessage().trim().length > 0,
  );

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

    this.senderId.set(this.resolveSenderId());
  }

  ngOnDestroy(): void {
    this.chatSessionService.disconnect();
  }

  connect(): void {
    this.messages.set([]);
    this.chatSessionService.connect(
      this.ticketId(),
      (message) => {
        this.messages.update((current) => [...current, message]);
      },
      (status) => {
        this.connectionStatus.set(
          status as 'déconnecté' | 'connexion...' | 'connecté' | 'erreur websocket',
        );
      },
    );
  }

  disconnect(): void {
    this.chatSessionService.disconnect();
    this.connectionStatus.set('déconnecté');
  }

  sendMessage(): void {
    this.chatSessionService.sendMessage(this.senderId(), this.draftMessage());
    this.draftMessage.set('');
  }

  protected handleSubmit(event: SubmitEvent): void {
    event.preventDefault();
    this.sendMessage();
  }

  protected updateTicketId(event: Event): void {
    this.ticketId.set(this.readInputValue(event));
  }

  protected updateSenderId(event: Event): void {
    this.senderId.set(this.readInputValue(event));
  }

  protected updateDraftMessage(event: Event): void {
    this.draftMessage.set(this.readInputValue(event));
  }

  private resolveSenderId(): string {
    const currentUser = this.authService.currentUser();

    if (!currentUser) {
      return '';
    }

    const knownSenderIds: Record<string, string> = {
      user: '21111111-1111-1111-1111-111111111111',
      admin: '22222222-2222-2222-2222-222222222222',
    };

    return knownSenderIds[currentUser.username] ?? '';
  }

  private readInputValue(event: Event): string {
    return (event.target as HTMLInputElement).value;
  }
}

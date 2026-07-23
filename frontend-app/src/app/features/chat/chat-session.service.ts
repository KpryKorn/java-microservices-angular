import { Injectable } from '@angular/core';
import { Client, IMessage, StompSubscription } from '@stomp/stompjs';
import { environment } from '../../../environments/environment';

export interface ChatTicketMessage {
  id?: string;
  ticketId: string;
  senderId: string;
  content: string;
  sentAt: string;
}

@Injectable({ providedIn: 'root' })
export class ChatSessionService {
  private client?: Client;
  private subscription?: StompSubscription;
  private activeTicketId = '';

  connect(
    ticketId: string,
    onMessage: (message: ChatTicketMessage) => void,
    onStatus: (status: string) => void,
  ): void {
    this.disconnect();

    this.activeTicketId = ticketId;
    onStatus('connexion...');

    const websocketUrl = new URL('/ws', environment.gatewayUrl).toString().replace('http', 'ws');

    this.client = new Client({
      brokerURL: websocketUrl,
      reconnectDelay: 0,
      debug: () => undefined,
    });

    this.client.onConnect = () => {
      this.subscription = this.client?.subscribe(
        `/topic/tickets/${ticketId}`,
        (message: IMessage) => {
          onMessage(JSON.parse(message.body) as ChatTicketMessage);
        },
      );
      onStatus('connecté');
    };

    this.client.onWebSocketClose = () => onStatus('déconnecté');
    this.client.onStompError = (frame) => onStatus(frame.headers['message'] ?? 'erreur websocket');

    this.client.activate();
  }

  sendMessage(senderId: string, content: string): void {
    if (!this.client?.connected || !this.activeTicketId || !content.trim()) {
      return;
    }

    this.client.publish({
      destination: `/app/tickets/${this.activeTicketId}/messages`,
      body: JSON.stringify({ senderId, content }),
    });
  }

  disconnect(): void {
    this.subscription?.unsubscribe();
    this.subscription = undefined;
    void this.client?.deactivate();
    this.client = undefined;
  }
}

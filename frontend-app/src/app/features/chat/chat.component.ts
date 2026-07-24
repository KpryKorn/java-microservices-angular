import { DatePipe } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, OnDestroy, OnInit, computed, inject, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { environment } from '../../../environments/environment';
import { ChatTicketMessage, Ticket } from '../../core/models/ticket.model';
import { AuthService } from '../../core/services/auth.service';
import { ChatSessionService } from './chat-session.service';
import { ChatTicketService } from './chat-ticket.service';

@Component({
  selector: 'app-chat',
  imports: [DatePipe],
  templateUrl: './chat.component.html',
})
export class ChatComponent implements OnInit, OnDestroy {
  private readonly http = inject(HttpClient);
  private readonly route = inject(ActivatedRoute);
  private readonly chatSessionService = inject(ChatSessionService);
  private readonly chatTicketService = inject(ChatTicketService);
  protected readonly authService = inject(AuthService);

  protected readonly chatStatus = signal('chargement...');
  protected readonly connectionStatus = signal<
    'déconnecté' | 'connexion...' | 'connecté' | 'erreur websocket'
  >('déconnecté');

  protected readonly ticketId = signal<string>('');
  protected readonly senderId = signal<string>('');
  protected readonly userTickets = signal<Ticket[]>([]);
  protected readonly draftMessage = signal('');
  protected readonly messages = signal<ChatTicketMessage[]>([]);
  protected readonly showCreateForm = signal(false);
  protected readonly createSubject = signal('');
  protected readonly createMotif = signal('');
  protected readonly createLoading = signal(false);
  protected readonly createError = signal('');

  protected readonly canSend = computed(
    () =>
      this.connectionStatus() === 'connecté' &&
      this.senderId().trim().length > 0 &&
      this.draftMessage().trim().length > 0 &&
      this.ticketId().trim().length > 0,
  );

  ngOnInit(): void {
    this.http
      .get(`${environment.gatewayUrl}/api/chat/ping`, {
        responseType: 'text',
        withCredentials: true,
      })
      .subscribe({
        next: (res) => this.chatStatus.set('disponible'),
        error: () => this.chatStatus.set('indisponible'),
      });

    const currentUser = this.authService.currentUser();
    if (currentUser?.id) {
      this.senderId.set(currentUser.id);
    }

    const routeTicketId = this.route.snapshot.paramMap.get('ticketId');
    if (routeTicketId) {
      this.ticketId.set(routeTicketId);
    } else {
      this.loadUserTickets();
    }
  }

  ngOnDestroy(): void {
    this.chatSessionService.disconnect();
  }

  private loadUserTickets(): void {
    this.chatTicketService.getUserTickets().subscribe({
      next: (tickets) => {
        this.userTickets.set(tickets);
        if (tickets.length > 0) {
          this.ticketId.set(tickets[0].id);
        }
      },
      error: (err) => console.error('Erreur lors de la récupération des tickets', err),
    });
  }

  connect(): void {
    if (!this.ticketId()) return;

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

  protected selectTicket(id: string): void {
    this.ticketId.set(id);
  }

  protected updateDraftMessage(event: Event): void {
    this.draftMessage.set((event.target as HTMLInputElement).value);
  }

  protected updateTicketId(event: Event): void {
    const target = event.target as HTMLSelectElement | HTMLInputElement;
    this.ticketId.set(target.value);
  }

  toggleCreateForm() {
    this.showCreateForm.update((v) => !v);
  }

  updateCreateSubject(e: Event) {
    this.createSubject.set((e.target as HTMLInputElement).value);
  }

  updateCreateMotif(e: Event) {
    this.createMotif.set((e.target as HTMLSelectElement).value);
  }

  handleCreateTicket(e: Event) {
    e.preventDefault();
    this.createLoading.set(true);
    this.createError.set('');

    this.chatTicketService
      .createTicket({
        subject: this.createSubject(),
        motif: this.createMotif(),
      })
      .subscribe({
        next: (ticket) => {
          this.createLoading.set(false);
          this.showCreateForm.set(false);
          this.createSubject.set('');
          this.createMotif.set('');
          this.ticketId.set(ticket.id);
          this.loadUserTickets();
          this.connect();
        },
        error: (err) => {
          this.createLoading.set(false);
          this.createError.set('Une erreur est survenue :' + err);
        },
      });
  }
}

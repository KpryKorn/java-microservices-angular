import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Ticket } from '../../core/models/ticket.model';

@Injectable({
  providedIn: 'root',
})
export class ChatTicketService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.gatewayUrl}/api/chat/tickets`;

  getUserTickets(): Observable<Ticket[]> {
    return this.http.get<Ticket[]>(this.baseUrl, { withCredentials: true });
  }
}

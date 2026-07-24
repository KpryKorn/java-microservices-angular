export type ChatTicketMessage = {
  id?: string;
  ticketId: string;
  senderId: string;
  senderUsername: string;
  content: string;
  sentAt: string;
};

export type Ticket = {
  id: string;
  userId: string;
  subject: string;
  motif?: string;
  status: 'OPEN' | 'RESOLVED' | 'CLOSED';
  createdAt: string;
  resolvedAt?: string;
};

export type CreateTicketRequest = Pick<Ticket, 'subject' | 'motif'>;

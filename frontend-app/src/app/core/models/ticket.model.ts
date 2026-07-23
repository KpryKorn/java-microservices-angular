export type Ticket = {
  id: string;
  userId: string;
  subject: string;
  motif?: string;
  status: 'OPEN' | 'RESOLVED' | 'CLOSED';
  createdAt: string;
  resolvedAt?: string;
};

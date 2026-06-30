import { HttpClient } from '@angular/common/http';
import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.component.html',
})
export class App implements OnInit {
  private http = inject(HttpClient);

  protected readonly title = signal('frontend-app');

  protected chatGWIStatus = signal('loading..');

  ngOnInit(): void {
    this.http
      .get('http://localhost:8080/api/chat/ping', { responseType: 'text' })
      .subscribe((res) => this.chatGWIStatus.set(res));
}

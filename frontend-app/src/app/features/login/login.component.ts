import { Component, OnInit, inject } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-login',
  imports: [],
  templateUrl: './login.component.html',
})
export class LoginComponent implements OnInit {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  ngOnInit(): void {
    // Si une session est déjà active (retour arrière navigateur, etc.), on saute la page.
    this.authService.checkSession().subscribe((user) => {
      if (user) {
        this.router.navigateByUrl('/chat');
      }
    });
  }

  login(): void {
    this.authService.login();
  }
}

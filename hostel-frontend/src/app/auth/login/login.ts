import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './login.html',
  styleUrls: ['./login.css']
})
export class Login {

  credentials = {
    username: '',
    password: ''
  };

  errorMessage = '';
  loading = false;

  constructor(
    private auth: AuthService,
    private router: Router
  ) {}

  onSubmit(): void {
    this.errorMessage = '';
    this.loading = true;

    this.auth.login(
      this.credentials.username,
      this.credentials.password
    ).subscribe({
      next: () => {
        this.loading = false;
        console.log('✅ OTP sent');
        this.router.navigate(['/otp']);
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage =
          err?.error?.message || 'Invalid username or password';
      }
    });
  }

  goToRegister(): void {
    this.router.navigate(['/register']);
  }
}

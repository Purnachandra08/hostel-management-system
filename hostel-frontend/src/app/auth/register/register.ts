import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './register.html',
  styleUrls: ['./register.css']
})
export class Register {

  model = {
    fullName: '',
    username: '',
    email: '',
    phone: '',
    password: ''
  };

  message = '';
  error = '';
  loading = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  onSubmit(): void {
    this.error = '';
    this.message = '';
    this.loading = true;

    // ✅ EXACTLY what backend expects
    const payload = {
      fullName: this.model.fullName,
      username: this.model.username,
      email: this.model.email,
      phone: this.model.phone,
      password: this.model.password
    };

    console.log('📤 Student Register Payload:', payload);

    this.authService.register(payload).subscribe({
      next: () => {
        this.loading = false;
        this.message = '✅ Registration successful. Please login.';

        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 1200);
      },
      error: (err) => {
        this.loading = false;
        this.error =
          err?.error?.message ||
          '❌ Registration failed. Username or email may already exist.';
      }
    });
  }

  goToLogin(): void {
    this.router.navigate(['/login']);
  }
}

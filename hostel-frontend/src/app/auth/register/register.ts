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

  // ✅ Allow only numbers in phone field
  allowOnlyNumbers(event: KeyboardEvent): void {
    const charCode = event.which ? event.which : event.keyCode;

    if (charCode < 48 || charCode > 57) {
      event.preventDefault();
    }
  }

  onSubmit(): void {
    this.error = '';
    this.message = '';

    // ✅ Extra safety validation
    if (!/^[A-Za-z ]{3,}$/.test(this.model.fullName)) {
      this.error = 'Invalid full name';
      return;
    }

    if (!/^[0-9]{10}$/.test(this.model.phone)) {
      this.error = 'Invalid phone number';
      return;
    }

    if (!/^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{6,}$/.test(this.model.password)) {
      this.error = 'Password must contain letters and numbers';
      return;
    }

    this.loading = true;

    const payload = { ...this.model };

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
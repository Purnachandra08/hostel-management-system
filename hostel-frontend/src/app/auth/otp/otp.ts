import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-otp',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './otp.html',
  styleUrls: ['./otp.css']
})
export class Otp {

  otp: string = '';
  loading: boolean = false;
  error: string = '';
  success: string = '';

  constructor(
    private auth: AuthService,
    private router: Router
  ) {}

  verifyOtp(): void {
    this.error = '';
    this.success = '';
    this.loading = true;

    if (!this.otp || this.otp.length < 4) {
      this.loading = false;
      this.error = 'Please enter a valid OTP';
      return;
    }

    this.auth.verifyOtp(this.otp).subscribe({
      next: () => {
        this.loading = false;

        const role = this.auth.getRole();

        if (!role) {
          this.error = 'Role not found after login';
          return;
        }

        this.success = 'OTP verified successfully';

        // 🚀 Role-based redirect
        switch (role) {
          case 'STUDENT':
            this.router.navigate(['/student']);
            break;

          case 'WARDEN':
            this.router.navigate(['/warden']);
            break;

          case 'ADMIN':
            this.router.navigate(['/admin']);
            break;

          default:
            this.error = 'Unauthorized role';
        }
      },
      error: (err) => {
        this.loading = false;
        this.error = err?.error?.message || 'Invalid or expired OTP';
      }
    });
  }

  backToLogin(): void {
    this.router.navigate(['/login']);
  }
}

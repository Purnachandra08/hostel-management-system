import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-pay-fee',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './pay-fee.html',
  styleUrls: ['./pay-fee.css']
})
export class PayFeeComponent implements OnInit {

  booking: any = null;
  paymentHistory: any[] = [];

  loading = false;
  paying = false;

  message: string = '';
  error: string = '';

  userId!: number;

  private bookingUrl = 'http://localhost:8080/api/bookings';
  private paymentUrl = 'http://localhost:8080/api/payments';

  constructor(
    private http: HttpClient,
    public authService: AuthService
  ) {}

  ngOnInit() {
    const user = this.authService.getUser();

    if (!user) {
      this.error = 'User not logged in';
      return;
    }

    this.userId = user.id;
    this.loadAll();
  }

  // =========================
  // LOAD ALL DATA
  // =========================
  loadAll() {
    this.getBooking();
    this.getPaymentHistory();
  }

  // =========================
  // GET BOOKING DETAILS
  // =========================
  getBooking() {
    this.loading = true;
    this.error = '';
    this.message = '';

    this.http.get(`${this.bookingUrl}/student/${this.userId}`)
      .subscribe({
        next: (res) => {
          this.booking = res;
          this.loading = false;
        },
        error: (err) => {
          this.booking = null;
          this.loading = false;
          this.error = err?.error?.message || 'No booking found';
        }
      });
  }

  // =========================
  // PAY ROOM FEE
  // =========================
  payNow() {

    if (!this.booking || this.booking.status === 'APPROVED') return;

    this.paying = true;
    this.error = '';
    this.message = '';

    this.http.post(`${this.paymentUrl}/room/${this.userId}`, {})
      .subscribe({
        next: () => {
          this.message = 'Payment successful 🎉';
          this.paying = false;

          // 🔄 Refresh data
          this.loadAll();
        },
        error: (err) => {
          this.error = err?.error?.message || 'Payment failed';
          this.paying = false;
        }
      });
  }

  // =========================
  // PAYMENT HISTORY
  // =========================
  getPaymentHistory() {
    this.http.get<any[]>(`${this.paymentUrl}/history/${this.userId}`)
      .subscribe({
        next: (res) => this.paymentHistory = res,
        error: () => this.paymentHistory = []
      });
  }

  // =========================
  // LOGOUT
  // =========================
  logout() {
    this.authService.logout();
  }
}
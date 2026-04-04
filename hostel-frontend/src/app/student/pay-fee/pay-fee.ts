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
  messFees: any[] = [];

  loading = false;
  paying = false;

  message: string = '';
  error: string = '';

  userId!: number;

  private bookingUrl = 'http://localhost:8080/api/bookings';
  private paymentUrl = 'http://localhost:8080/api/payments';
  private messFeeUrl = 'http://localhost:8080/api/mess-fees';

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
  // LOAD ALL
  // =========================
  loadAll() {
    this.getBooking();
    this.getPaymentHistory();
    this.getMessFees();
  }

  // =========================
  // GET BOOKING
  // =========================
  getBooking() {
    this.loading = true;
    this.clearMessages();

    this.http.get(`${this.bookingUrl}/student/${this.userId}`)
      .subscribe({
        next: (res) => {
          this.booking = res;
          this.loading = false;
        },
        error: () => {
          this.booking = null;
          this.loading = false;
        }
      });
  }

  // =========================
  // PAY ROOM
  // =========================
  payNow() {

    if (!this.booking) {
      this.error = 'No booking found';
      return;
    }

    if (this.booking.status === 'APPROVED') {
      this.error = 'Already paid';
      return;
    }

    this.paying = true;
    this.clearMessages();

    this.http.post(`${this.paymentUrl}/room/${this.userId}`, {})
      .subscribe({
        next: () => {
          this.message = 'Payment successful 🎉';
          this.paying = false;
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
        next: (res) => {
          this.paymentHistory = (res || []).filter(p =>
            p.amount > 0 && p.type && p.status
          );
        },
        error: () => {
          this.paymentHistory = [];
        }
      });
  }

  // =========================
  // GET MESS FEES
  // =========================
  getMessFees() {
    this.http.get<any[]>(`${this.messFeeUrl}/${this.userId}`)
      .subscribe({
        next: (res) => {
          this.messFees = (res || []).sort((a, b) => a.month - b.month);
        },
        error: () => {
          this.messFees = [];
        }
      });
  }

  // =========================
  // PAY MESS
  // =========================
  payMess(messId: number) {

    this.paying = true;
    this.clearMessages();

    this.http.post(`${this.paymentUrl}/mess/${messId}`, {})
      .subscribe({
        next: () => {
          this.message = 'Mess fee paid successfully 🍽️';
          this.paying = false;
          this.loadAll();
        },
        error: (err) => {
          this.error = err?.error?.message || 'Mess payment failed';
          this.paying = false;
        }
      });
  }

  // =========================
  // MONTH NAME
  // =========================
  getMonthName(month: number): string {
    const months = [
      'January', 'February', 'March', 'April',
      'May', 'June', 'July', 'August',
      'September', 'October', 'November', 'December'
    ];
    return months[month - 1] || '';
  }

  // =========================
  clearMessages() {
    this.message = '';
    this.error = '';
  }

  logout() {
    this.authService.logout();
  }
}
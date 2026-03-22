import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PaymentService, FeeDetails, PaymentHistory } from '../../services/payment.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-pay-fee',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './pay-fee.html',
  styleUrls: ['./pay-fee.css']
})
export class PayFeeComponent implements OnInit {

  fee: FeeDetails | null = null;
  loading = false;
  paying = false;
  message: string = '';
  userId!: number;

  paymentHistory: PaymentHistory[] = [];

  constructor(
    private service: PaymentService,
    public authService: AuthService
  ) {}

  ngOnInit() {
    const user = this.authService.getUser();
    if (!user) return;

    this.userId = user.id;

    this.getFee();
    this.loadHistory();
  }

  // ✅ Month Name Converter
  getMonthName(month: number): string {
    return new Date(0, month - 1).toLocaleString('en', { month: 'long' });
  }

  getFee() {
    this.loading = true;

    this.service.getFeeDetails(this.userId).subscribe({
      next: (res) => {
        this.fee = res;
        this.loading = false;
      },
      error: () => {
        this.message = 'No fee available';
        this.loading = false;
      }
    });
  }

  payNow() {
    if (!this.fee || this.fee.paymentStatus === 'PAID') return;

    this.paying = true;

    this.service.payFee(this.userId).subscribe({
      next: () => {
        alert('Payment Successful!');
        this.paying = false;
        this.getFee();
        this.loadHistory();
      },
      error: () => {
        this.paying = false;
        alert('Payment Failed');
      }
    });
  }

  loadHistory() {
    this.service.getPaymentHistory(this.userId).subscribe(res => {
      this.paymentHistory = res;
    });
  }
}
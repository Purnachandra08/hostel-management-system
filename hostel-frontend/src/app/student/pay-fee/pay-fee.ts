import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PaymentService, FeeDetails } from '../../services/payment.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-pay-fee',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './pay-fee.html',
  styleUrls: ['./pay-fee.css']
})
export class PayFeeComponent implements OnInit {

  fee: FeeDetails | null = null; // ✅ typed fee
  loading = false;
  paying = false;
  message: string = ''; // ✅ declare message for error or info
  userId!: number; // set from AuthService

  constructor(
    private service: PaymentService,
    public authService: AuthService
  ) {}

  ngOnInit() {
    const user = this.authService.getUser();
    if (!user) {
      alert('User not logged in.');
      return;
    }
    this.userId = user.id;
    this.getFee();
  }

  // =========================
  // Get Fee Details
  // =========================
  getFee() {
    this.loading = true;
    this.message = '';

    this.service.getFeeDetails(this.userId).subscribe({
      next: (res) => {
        this.fee = res;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error fetching fee:', err);

        // Friendly message for no booking or 400 error
        if (err.status === 400 || err.message.includes('No fee details')) {
          this.fee = null;
          this.message = 'No fee details available. Please book a room first.';
        } else {
          this.message = 'Failed to load fee details. Try again later.';
        }

        this.loading = false;
      }
    });
  }

  // =========================
  // Pay Fee
  // =========================
  payNow() {
    if (!this.fee || this.fee.paymentStatus === 'PAID') return;

    this.paying = true;
    this.service.payFee(this.userId).subscribe({
      next: (res) => {
        this.fee!.paymentStatus = 'PAID';
        this.paying = false;
        alert('Payment Successful!');
      },
      error: (err) => {
        console.error('Payment error:', err);
        this.paying = false;
        alert('Payment Failed. Try again!');
      }
    });
  }
}
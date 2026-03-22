import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PaymentService, PaymentHistory } from '../../services/payment.service';

@Component({
  selector: 'app-admin-payments',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './admin-payments.html',
  styleUrls: ['./admin-payments.css']
})
export class AdminPaymentsComponent implements OnInit {

  payments: PaymentHistory[] = [];
  total = 0;

  constructor(private service: PaymentService) {}

  ngOnInit() {
    this.loadPayments();
  }

  loadPayments() {
    this.service.getAllPayments().subscribe({
      next: (res: PaymentHistory[]) => {
        this.payments = res;

        // ✅ Calculate total revenue
        this.total = res
          .filter(p => p.paymentStatus === 'PAID')
          .reduce((sum, p) => sum + p.totalFee, 0);
      },
      error: (err) => {
        console.error('Error loading payments', err);
      }
    });
  }
}
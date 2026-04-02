import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PaymentService, Payment } from '../../services/payment.service';

@Component({
  selector: 'app-admin-payments',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './admin-payments.html',
  styleUrls: ['./admin-payments.css']
})
export class AdminPaymentsComponent implements OnInit {

  payments: Payment[] = [];
  total = 0;

  constructor(private service: PaymentService) {}

  ngOnInit() {
    this.loadPayments();
  }

  loadPayments() {
    this.service.getAllPayments().subscribe({
      next: (res: Payment[]) => {
        this.payments = res;

        // ✅ Total revenue (only SUCCESS payments)
        this.total = res
          .filter(p => p.status === 'SUCCESS')
          .reduce((sum, p) => sum + p.amount, 0);
      },
      error: (err) => {
        console.error('Error loading payments', err);
        this.payments = [];
      }
    });
  }
}
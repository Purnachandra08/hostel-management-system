import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PaymentService } from '../../services/payment.service';

@Component({
  selector: 'app-admin-payments',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './admin-payments.html',
  styleUrls: ['./admin-payments.css']
})
export class AdminPaymentsComponent implements OnInit {

  payments: any[] = [];
  total = 0;

  constructor(private service: PaymentService) {}

  ngOnInit() {
    this.service.getAllPayments().subscribe(res => {
      this.payments = res;

      this.total = res
        .filter((p: any) => p.paymentStatus === 'PAID')
        .reduce((sum: number, p: any) => sum + p.totalFee, 0);
    });
  }
}
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

// ==============================
// ✅ Payment Model (FIXED)
// ==============================
export interface Payment {
  id: number;
  userId: number;   // ✅ FIX ADDED
  amount: number;
  type: 'ROOM' | 'MESS';
  status: 'SUCCESS' | 'FAILED';
  paymentDate: string;
}

@Injectable({
  providedIn: 'root'
})
export class PaymentService {

  private baseUrl = 'http://localhost:8080/api/payments';
  private adminUrl = 'http://localhost:8080/api/admin/payments';

  constructor(private http: HttpClient) {}

  // 💰 PAY ROOM FEE
  payRoomFee(userId: number): Observable<Payment> {
    return this.http.post<Payment>(
      `${this.baseUrl}/room/${userId}`, {}
    );
  }

  // 🍽️ PAY MESS FEE
  payMessFee(messId: number): Observable<Payment> {
    return this.http.post<Payment>(
      `${this.baseUrl}/mess/${messId}`, {}
    );
  }

  // 📜 USER PAYMENT HISTORY
  getPaymentHistory(userId: number): Observable<Payment[]> {
    return this.http.get<Payment[]>(
      `${this.baseUrl}/history/${userId}`
    ).pipe(
      catchError(() => of([]))
    );
  }

  // 📊 ADMIN: ALL PAYMENTS
  getAllPayments(): Observable<Payment[]> {
    return this.http.get<Payment[]>(
      this.adminUrl
    ).pipe(
      catchError(() => of([]))
    );
  }
}
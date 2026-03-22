import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

// ==============================
// ✅ Fee Details (Monthly)
// ==============================
export interface FeeDetails {
  roomFee: number;
  messFee: number;
  totalFee: number;
  paymentStatus: 'PAID' | 'PENDING';
  paymentDate?: string;
  month: number;
  year: number;
}

// ==============================
// ✅ Payment History (Monthly)
// ==============================
export interface PaymentHistory {
  id: number;
  roomFee: number;
  messFee: number;
  totalFee: number;
  paymentStatus: 'PAID' | 'PENDING';
  paymentDate?: string;
  month: number;
  year: number;
}

@Injectable({
  providedIn: 'root'
})
export class PaymentService {

  private baseUrl = 'http://localhost:8080/api/payments';

  constructor(private http: HttpClient) {}

  // ✅ Student Fee
  getFeeDetails(userId: number): Observable<FeeDetails | null> {
    return this.http.get<FeeDetails>(`${this.baseUrl}/fee/${userId}`).pipe(
      catchError(() => of(null))
    );
  }

  // ✅ Pay Fee
  payFee(userId: number): Observable<FeeDetails> {
    return this.http.post<FeeDetails>(`${this.baseUrl}/pay/${userId}`, {});
  }

  // ✅ Student History
  getPaymentHistory(userId: number): Observable<PaymentHistory[]> {
    return this.http.get<PaymentHistory[]>(`${this.baseUrl}/history/${userId}`).pipe(
      catchError(() => of([]))
    );
  }

  // ✅ ADMIN: Get All Payments  🔥 (FIXED)
  getAllPayments(): Observable<PaymentHistory[]> {
    return this.http.get<PaymentHistory[]>(`${this.baseUrl}/all`).pipe(
      catchError(() => of([]))
    );
  }
}
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

// ✅ Define a type for Fee Details
export interface FeeDetails {
  roomFee: number;
  messFee: number;
  totalFee: number;
  paymentStatus: 'PAID' | 'PENDING';
}

export interface PaymentHistory {
  id: number;
  amount: number;
  date: string;
  status: 'PAID' | 'PENDING';
}

@Injectable({
  providedIn: 'root'
})
export class PaymentService {

  private baseUrl = 'http://localhost:8080/api/payments';

  constructor(private http: HttpClient) {}

  // ==============================
  // Get Fee Details for a Student
  // ==============================
  getFeeDetails(userId: number): Observable<FeeDetails | null> {
    return this.http.get<FeeDetails>(`${this.baseUrl}/fee/${userId}`)
      .pipe(
        catchError(err => {
          console.error('Error fetching fee details', err);
          // Return null if no booking exists or 400 error occurs
          return throwError(() => new Error(err?.error?.message || 'No fee details found'));
        })
      );
  }

  // ==============================
  // Pay Fee for a Student
  // ==============================
  payFee(userId: number): Observable<FeeDetails> {
    return this.http.post<FeeDetails>(`${this.baseUrl}/pay/${userId}`, {})
      .pipe(
        catchError(err => {
          console.error('Error paying fee', err);
          return throwError(() => new Error(err?.error?.message || 'Payment failed'));
        })
      );
  }

  // ==============================
  // Get Payment History
  // ==============================
  getPaymentHistory(userId: number): Observable<PaymentHistory[]> {
    return this.http.get<PaymentHistory[]>(`${this.baseUrl}/history/${userId}`)
      .pipe(
        catchError(err => {
          console.error('Error fetching payment history', err);
          return throwError(() => new Error('Failed to fetch payment history'));
        })
      );
  }

  // ==============================
  // Get All Payments (Admin)
  // ==============================
  getAllPayments(): Observable<PaymentHistory[]> {
    return this.http.get<PaymentHistory[]>(`${this.baseUrl}/all`)
      .pipe(
        catchError(err => {
          console.error('Error fetching all payments', err);
          return throwError(() => new Error('Failed to fetch all payments'));
        })
      );
  }
}
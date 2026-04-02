import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class BookingService {

  private baseUrl = 'http://localhost:8080/api/bookings';

  constructor(private http: HttpClient) {}

  // ✅ Book Room (MATCH BACKEND)
  bookRoom(userId: number, roomId: number): Observable<any> {
    return this.http.post(`${this.baseUrl}/${userId}/${roomId}`, {});
  }

  approveBooking(bookingId: number): Observable<any> {
  return this.http.put(`${this.baseUrl}/approve/${bookingId}`, {});
}

  // ✅ Get Student Booking (CURRENT YEAR)
  getStudentBooking(userId: number): Observable<any> {
    return this.http.get(`${this.baseUrl}/student/${userId}`);
  }

  // ✅ Get All Bookings (ADMIN)
  getAllBookings(): Observable<any> {
    return this.http.get(`${this.baseUrl}`);
  }

  // ✅ Cancel Booking
  cancelBooking(bookingId: number): Observable<any> {
    return this.http.put(`${this.baseUrl}/${bookingId}/cancel`, {});
  }
}
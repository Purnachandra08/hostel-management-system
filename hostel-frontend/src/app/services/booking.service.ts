import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class BookingService {
  private baseUrl = 'http://localhost:8080/api/bookings';

  constructor(private http: HttpClient) {}

  // ✅ Create a new booking
  bookRoom(userId: number, roomId: number, bookingData: any): Observable<any> {
    // No query params — pass IDs in request body instead (cleaner & backend-friendly)
    const payload = {
      userId: userId,
      roomId: roomId,
      startDate: bookingData.startDate,
      endDate: bookingData.endDate
    };
    return this.http.post(`${this.baseUrl}/create`, payload);
  }

  // ✅ Get all bookings
  getAllBookings(): Observable<any> {
    return this.http.get(`${this.baseUrl}/all`);
  }

  // ✅ Cancel booking
  cancelBooking(bookingId: number): Observable<any> {
    return this.http.put(`${this.baseUrl}/cancel/${bookingId}`, {});
  }

  // ✅ Get booking by user ID
  getBookingsByUser(userId: number): Observable<any> {
    return this.http.get(`${this.baseUrl}/user/${userId}`);
  }
}

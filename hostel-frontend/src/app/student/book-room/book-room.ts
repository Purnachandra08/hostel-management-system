import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RoomService } from '../../services/room.service';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-book-room',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './book-room.html',
  styleUrls: ['./book-room.css']
})
export class BookRoom implements OnInit {

  rooms: any[] = [];
  studentBooking: any = null;

  message: string = '';
  error: string = '';
  loading: boolean = false;

  currentYear: number = new Date().getFullYear();

  private bookingBaseUrl = 'http://localhost:8080/api/bookings';
  private paymentBaseUrl = 'http://localhost:8080/api/payments';

  constructor(
    private roomService: RoomService,
    private http: HttpClient,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadAll();
  }

  // =========================
  // LOAD ALL DATA
  // =========================
  loadAll() {
    this.loadAvailableRooms();
    this.loadStudentBooking();
  }

  // =========================
  // CLEAR MESSAGES
  // =========================
  clearMessages() {
    this.message = '';
    this.error = '';
  }

  // =========================
  // LOAD ROOMS
  // =========================
  loadAvailableRooms(): void {
    this.roomService.getAvailableRooms().subscribe({
      next: (data) => this.rooms = data || [],
      error: () => this.error = 'Failed to load rooms'
    });
  }

  // =========================
  // LOAD BOOKING
  // =========================
  loadStudentBooking(): void {
    const user = this.authService.getUser();
    if (!user) return;

    this.http.get(`${this.bookingBaseUrl}/student/${user.id}`).subscribe({
      next: (data) => this.studentBooking = data,
      error: () => this.studentBooking = null
    });
  }

  // =========================
  // BOOK ROOM
  // =========================
  bookRoom(roomId: number): void {

    const user = this.authService.getUser();
    if (!user) {
      this.error = 'Please login again';
      return;
    }

    this.loading = true;
    this.clearMessages();

    this.http.post(`${this.bookingBaseUrl}/${user.id}/${roomId}`, {})
      .subscribe({
        next: (res: any) => {
          this.message = res.message || 'Booking created successfully';
          this.loading = false;
          this.loadAll();
        },
        error: (err) => {
          this.error = err?.error?.message || 'Booking failed';
          this.loading = false;
        }
      });
  }

  // =========================
  // PAY ROOM FEE
  // =========================
  payRoomFee(): void {

    const user = this.authService.getUser();
    if (!user) return;

    if (this.studentBooking?.status === 'APPROVED') {
      this.error = 'Already paid';
      return;
    }

    this.loading = true;
    this.clearMessages();

    this.http.post(`${this.paymentBaseUrl}/room/${user.id}`, {})
      .subscribe({
        next: () => {
          this.message = 'Payment successful 🎉';
          this.loading = false;
          this.loadAll();
        },
        error: (err) => {
          this.error = err?.error?.message || 'Payment failed';
          this.loading = false;
        }
      });
  }

  // =========================
  // LOGOUT
  // =========================
  logout(): void {
    this.authService.logout();
  }
}
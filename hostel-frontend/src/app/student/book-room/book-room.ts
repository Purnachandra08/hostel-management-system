import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RoomService } from '../../services/room.service';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-book-room',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './book-room.html',
  styleUrls: ['./book-room.css']
})
export class BookRoom implements OnInit {

  rooms: any[] = [];
  selectedRoomId: number | null = null;
  message: string = '';
  currentYear: number = new Date().getFullYear();
  studentBooking: any = null; // ✅ Store current student booking

  private bookingBaseUrl = 'http://localhost:8080/api/bookings';
  private roomBaseUrl = 'http://localhost:8080/api/rooms';

  constructor(
    private roomService: RoomService,
    private http: HttpClient,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadAvailableRooms();
    this.loadStudentBooking(); // ✅ Load current booking on page load
  }

  // ================================
  // Load Available Rooms
  // ================================
  loadAvailableRooms(): void {
    this.roomService.getAvailableRooms().subscribe({
      next: (data) => {
        this.rooms = data;
      },
      error: (err) => {
        console.error('Error loading rooms:', err);
        this.message = 'Failed to load available rooms.';
      }
    });
  }

  // ================================
  // Load Current Student Booking
  // ================================
  loadStudentBooking(): void {
    const user = this.authService.getUser();
    if (!user) return;

    this.http.get(`${this.bookingBaseUrl}/student/${user.id}`).subscribe({
      next: (data) => {
        this.studentBooking = data; // Store booking details
      },
      error: (err) => {
        console.log('No active booking found or error fetching booking:', err);
        this.studentBooking = null;
      }
    });
  }

  // ================================
  // Book Room
  // ================================
  bookRoom(): void {

    if (!this.selectedRoomId) {
      this.message = 'Please select a room.';
      return;
    }

    const user = this.authService.getUser();
    if (!user) {
      this.message = 'User not logged in. Please login again.';
      return;
    }

    this.http.post(`${this.bookingBaseUrl}/${user.id}/${this.selectedRoomId}`, {}).subscribe({
      next: (res: any) => {
        this.message = 'Room booked successfully!';
        this.selectedRoomId = null;

        // Reload rooms and student booking
        this.loadAvailableRooms();
        this.loadStudentBooking();
      },
      error: (err) => {
        console.error('Error booking room:', err);
        if (err.error && err.error.message) {
          this.message = err.error.message;
        } else {
          this.message = 'Failed to book room. Please try again.';
        }
      }
    });
  }

  // ================================
  // Logout function
  // ================================
  logout(): void {
    this.authService.logout();
  }

}
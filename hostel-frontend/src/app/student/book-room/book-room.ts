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
  startDate: string = '';
  endDate: string = '';
  message: string = '';
  currentYear: number = new Date().getFullYear();

  private baseUrl = 'http://localhost:8080/api/bookings';

  constructor(
    private roomService: RoomService,
    private http: HttpClient,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadAvailableRooms();
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
  // Book Room
  // ================================
  bookRoom(): void {

    if (!this.selectedRoomId || !this.startDate || !this.endDate) {
      this.message = 'Please fill in all fields.';
      return;
    }

    // Get logged in user from AuthService
    const user = this.authService.getUser();

    if (!user) {
      this.message = 'User not logged in. Please login again.';
      return;
    }

    const booking = {
      user: { id: user.id },
      room: { id: this.selectedRoomId },
      startDate: this.startDate,
      endDate: this.endDate
    };

    console.log('Booking Data:', booking);

    this.http.post(`${this.baseUrl}`, booking).subscribe({
      next: () => {

        this.message = 'Room booked successfully!';

        // Reset form
        this.selectedRoomId = null;
        this.startDate = '';
        this.endDate = '';

        // Reload rooms
        this.loadAvailableRooms();
      },

      error: (err) => {

        console.error('Error booking room:', err);

        if (err.error) {
          this.message = err.error;
        } else {
          this.message = 'Failed to book room. Please try again.';
        }
      }
    });
  }
}
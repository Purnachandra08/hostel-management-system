import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RoomService } from '../../services/room.service';
import { HttpClient } from '@angular/common/http';

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

  constructor(private roomService: RoomService, private http: HttpClient) {}

  ngOnInit() {
    this.loadAvailableRooms();
  }

  loadAvailableRooms() {
    this.roomService.getAvailableRooms().subscribe({
      next: (data) => {
        this.rooms = data;
      },
      error: (err) => {
        console.error('Error loading rooms', err);
      }
    });
  }

  bookRoom() {
    if (!this.selectedRoomId || !this.startDate || !this.endDate) {
      this.message = 'Please fill in all fields.';
      return;
    }

    const userId = Number(localStorage.getItem('userId')); // Ensure userId is stored after login

    const booking = {
      userId,
      roomId: this.selectedRoomId,
      startDate: this.startDate,
      endDate: this.endDate
    };

    this.http.post(`${this.baseUrl}`, booking).subscribe({
      next: () => {
        this.message = 'Room booked successfully!';
        this.loadAvailableRooms();
      },
      error: (err) => {
        console.error('Error booking room:', err);
        this.message = 'Failed to book room. Please try again.';
      }
    });
  }
}

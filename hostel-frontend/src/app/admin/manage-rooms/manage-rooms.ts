import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule, CurrencyPipe } from '@angular/common';

@Component({
  selector: 'app-manage-rooms',
  standalone: true, // ✅ Standalone component (important)
  imports: [CommonModule, FormsModule, CurrencyPipe], // ✅ Fixes ngModel + currency pipe errors
  templateUrl: './manage-rooms.html',
  styleUrls: ['./manage-rooms.css']
})
export class ManageRooms implements OnInit {
  rooms: any[] = [];
  newRoom = {
    roomNumber: '',
    type: '',
    capacity: 1,
    pricePerMonth: 0,
  };

  private baseUrl = 'http://localhost:8080/api/rooms';

  constructor(private http: HttpClient, private router: Router) {}

  ngOnInit(): void {
    this.loadRooms();
  }

  // ✅ Fetch all rooms
  loadRooms(): void {
    this.http.get<any[]>(this.baseUrl).subscribe({
      next: (data) => (this.rooms = data),
      error: (err) => console.error('Error loading rooms', err)
    });
  }

  // ✅ Add a new room
  addRoom(): void {
    if (!this.newRoom.roomNumber || !this.newRoom.type) {
      alert('Please fill all required fields.');
      return;
    }

    this.http.post(this.baseUrl, this.newRoom).subscribe({
      next: () => {
        alert('Room added successfully!');
        this.loadRooms();
        this.newRoom = { roomNumber: '', type: '', capacity: 1, pricePerMonth: 0 };
      },
      error: (err) => console.error('Error adding room', err)
    });
  }

  // ✅ Delete room
  deleteRoom(id: number): void {
    if (confirm('Are you sure you want to delete this room?')) {
      this.http.delete(`${this.baseUrl}/${id}`).subscribe({
        next: () => {
          alert('Room deleted successfully!');
          this.loadRooms();
        },
        error: (err) => console.error('Error deleting room', err)
      });
    }
  }

  // ✅ Logout function
  logout(): void {
    localStorage.clear();
    sessionStorage.clear();
    this.router.navigate(['/login']);
  }
}

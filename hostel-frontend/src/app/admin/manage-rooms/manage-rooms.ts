import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule, CurrencyPipe } from '@angular/common';

@Component({
  selector: 'app-manage-rooms',
  standalone: true,
  imports: [CommonModule, FormsModule, CurrencyPipe],
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

  successMessage: string = '';
  errorMessage: string = '';

  private baseUrl = 'http://localhost:8080/api/rooms';

  constructor(private http: HttpClient, private router: Router) {}

  ngOnInit(): void {
    this.loadRooms();
  }

  // ✅ Fetch all rooms
  loadRooms(): void {
    this.http.get<any[]>(this.baseUrl).subscribe({
      next: (data) => this.rooms = data,
      error: (err) => this.showError('Failed to load rooms: ' + err.message)
    });
  }

  // ✅ Add a new room
  addRoom(): void {
    this.clearMessages();

    if (!this.newRoom.roomNumber || !this.newRoom.type) {
      this.showError('Please fill all required fields.');
      return;
    }

    this.http.post(this.baseUrl, this.newRoom).subscribe({
      next: (data) => {
        this.showSuccess('Room added successfully!');
        this.loadRooms();
        this.newRoom = { roomNumber: '', type: '', capacity: 1, pricePerMonth: 0 };
      },
      error: (err) => this.showError('Error adding room: ' + err.error?.message || err.message)
    });
  }

  // ✅ Delete room
  deleteRoom(id: number): void {
    this.clearMessages();

    if (!confirm('Are you sure you want to delete this room?')) return;

    this.http.delete(`${this.baseUrl}/${id}`).subscribe({
      next: () => {
        this.showSuccess('Room deleted successfully!');
        this.loadRooms();
      },
      error: (err) => this.showError('Error deleting room: ' + err.error?.message || err.message)
    });
  }

  // ✅ Logout
  logout(): void {
    localStorage.clear();
    sessionStorage.clear();
    this.router.navigate(['/login']);
  }

  // ✅ Helper functions
  private showSuccess(msg: string) {
    this.successMessage = msg;
    setTimeout(() => this.successMessage = '', 5000); // hide after 5s
  }

  private showError(msg: string) {
    this.errorMessage = msg;
    setTimeout(() => this.errorMessage = '', 7000); // hide after 7s
  }

  private clearMessages() {
    this.successMessage = '';
    this.errorMessage = '';
  }
}
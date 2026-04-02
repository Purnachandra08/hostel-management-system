import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { RoomService } from '../../services/room.service';

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

  // ✏️ Edit Mode
  isEditMode = false;
  editRoomId: number | null = null;

  successMessage: string = '';
  errorMessage: string = '';
  loading: boolean = false;

  constructor(private roomService: RoomService, private router: Router) {}

  ngOnInit(): void {
    this.loadRooms();
  }

  // ✅ Load rooms
  loadRooms(): void {
    this.loading = true;

    this.roomService.getAllRooms().subscribe({
      next: (data) => {
        this.rooms = data;
        this.loading = false;
      },
      error: (err) => {
        this.showError('Failed to load rooms');
        this.loading = false;
      }
    });
  }

  // ✅ Add OR Update Room
  saveRoom(): void {
    this.clearMessages();

    if (!this.newRoom.roomNumber || !this.newRoom.type) {
      this.showError('Please fill all required fields');
      return;
    }

    if (this.isEditMode && this.editRoomId !== null) {
      // ✏️ UPDATE
      this.roomService.updateRoom(this.editRoomId, this.newRoom).subscribe({
        next: () => {
          this.showSuccess('Room updated successfully!');
          this.resetForm();
          this.loadRooms();
        },
        error: (err) => this.showError(err.error?.message || 'Update failed')
      });

    } else {
      // ➕ CREATE
      this.roomService.addRoom(this.newRoom).subscribe({
        next: () => {
          this.showSuccess('Room added successfully!');
          this.resetForm();
          this.loadRooms();
        },
        error: (err) => this.showError(err.error?.message || 'Add failed')
      });
    }
  }

  // ✏️ Edit Room
  editRoom(room: any): void {
    this.newRoom = { ...room };
    this.editRoomId = room.id;
    this.isEditMode = true;
  }

  // ❌ Delete Room
  deleteRoom(id: number): void {
    if (!confirm('Are you sure you want to delete this room?')) return;

    this.roomService.deleteRoom(id).subscribe({
      next: () => {
        this.showSuccess('Room deleted successfully!');
        this.loadRooms();
      },
      error: (err) => this.showError(err.error?.message || 'Delete failed')
    });
  }

  // 🔄 Reset Form
  resetForm(): void {
    this.newRoom = {
      roomNumber: '',
      type: '',
      capacity: 1,
      pricePerMonth: 0
    };
    this.isEditMode = false;
    this.editRoomId = null;
  }

  // 🚪 Logout
  logout(): void {
    localStorage.clear();
    sessionStorage.clear();
    this.router.navigate(['/login']);
  }

  // 💬 Messages
  private showSuccess(msg: string) {
    this.successMessage = msg;
    setTimeout(() => this.successMessage = '', 4000);
  }

  private showError(msg: string) {
    this.errorMessage = msg;
    setTimeout(() => this.errorMessage = '', 5000);
  }

  private clearMessages() {
    this.successMessage = '';
    this.errorMessage = '';
  }
}
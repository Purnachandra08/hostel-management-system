import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminUserService } from '../../services/admin-user.service';

@Component({
  selector: 'app-manage-wardens',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './manage-wardens.html',
  styleUrls: ['./manage-wardens.css']
})
export class ManageWardens implements OnInit {

  // 🧑‍🏫 Warden Form Model
  warden = {
    fullName: '',
    email: '',
    username: '',
    password: '',
    phone: '',
    role: 'ROLE_WARDEN' as 'ROLE_WARDEN'
  };

  // 📋 Wardens List (THIS WAS MISSING)
  wardens: any[] = [];

  loading = false;
  successMessage = '';
  errorMessage = '';

  constructor(private adminUserService: AdminUserService) {}

  // ===============================
  // 🔄 LOAD WARDENS ON PAGE LOAD
  // ===============================
  ngOnInit(): void {
    this.loadWardens();
  }

  // ===============================
  // 👑 CREATE WARDEN
  // ===============================
  createWarden(): void {
    this.successMessage = '';
    this.errorMessage = '';

    if (
      !this.warden.fullName ||
      !this.warden.email ||
      !this.warden.username ||
      !this.warden.password ||
      !this.warden.phone
    ) {
      this.errorMessage = 'All fields are required';
      return;
    }

    this.loading = true;

    this.adminUserService.createUser(this.warden).subscribe({
      next: () => {
        this.loading = false;
        this.successMessage = '✅ Warden created successfully';

        // 🔄 Reset form
        this.warden = {
          fullName: '',
          email: '',
          username: '',
          password: '',
          phone: '',
          role: 'ROLE_WARDEN'
        };

        // 🔁 Reload table
        this.loadWardens();
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = err?.error || '❌ Failed to create warden';
      }
    });
  }

  // ===============================
  // 📋 GET ALL WARDENS
  // ===============================
  loadWardens(): void {
    this.adminUserService.getAllWardens().subscribe({
      next: (data) => {
        this.wardens = data;
      },
      error: () => {
        this.errorMessage = '❌ Failed to load wardens';
      }
    });
  }

  // ===============================
  // 🗑 DELETE WARDEN
  // ===============================
  deleteWarden(id: number): void {
    if (!confirm('Are you sure you want to delete this warden?')) return;

    this.adminUserService.deleteWarden(id).subscribe({
      next: () => {
        this.successMessage = '🗑 Warden deleted successfully';
        this.loadWardens();
      },
      error: () => {
        this.errorMessage = '❌ Failed to delete warden';
      }
    });
  }
}

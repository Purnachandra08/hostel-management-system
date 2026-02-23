import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-warden-complaints',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    FormsModule
  ],
  templateUrl: './warden-complaints.html',
  styleUrls: ['./warden-complaints.css']
})
export class WardenComplaints implements OnInit {

  complaints: any[] = [];
  loading = true;
  error = '';
  message = '';

  private readonly baseUrl = 'http://localhost:8080/api/complaints';

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.fetchComplaints();
  }

  // 🔄 Fetch all complaints
  fetchComplaints(): void {
    this.loading = true;

    this.http.get<any[]>(`${this.baseUrl}/all`).subscribe({
      next: (res) => {
        this.complaints = res.map(c => ({
          ...c,
          remark: c.remark || '',
          status: c.status || 'PENDING'
        }));
        this.loading = false;
      },
      error: () => {
        this.error = '❌ Failed to load complaints.';
        this.loading = false;
        setTimeout(() => (this.error = ''), 3000);
      }
    });
  }

  // ✅ Update complaint status dynamically
  updateStatus(id: number, status: string, remark: string): void {

    this.http.put(
      `${this.baseUrl}/update-status/${id}?status=${status}`,
      { remark }
    ).subscribe({
      next: () => {
        this.message = `✅ Complaint marked as ${status}!`;
        this.fetchComplaints();
        setTimeout(() => (this.message = ''), 3000);
      },
      error: () => {
        this.error = '❌ Error updating complaint.';
        setTimeout(() => (this.error = ''), 3000);
      }
    });
  }

  // 🗑 Optional: Delete complaint
  deleteComplaint(id: number): void {
    if (!confirm('Are you sure you want to delete this complaint?')) return;

    this.http.delete(`${this.baseUrl}/${id}`).subscribe({
      next: () => {
        this.message = '🗑 Complaint deleted successfully!';
        this.fetchComplaints();
        setTimeout(() => (this.message = ''), 3000);
      },
      error: () => {
        this.error = '❌ Error deleting complaint.';
        setTimeout(() => (this.error = ''), 3000);
      }
    });
  }
}
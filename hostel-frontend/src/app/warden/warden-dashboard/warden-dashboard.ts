import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-warden-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './warden-dashboard.html',
  styleUrls: ['./warden-dashboard.css']
})
export class WardenDashboard implements OnInit {

  wardenName: string = '';
  loading: boolean = false;
  error: string = '';

  summaryCards: any[] = [];

  private readonly baseUrl = 'http://localhost:8080/api/warden';

  constructor(
    private http: HttpClient,
    private router: Router,
    private auth: AuthService
  ) {}

  ngOnInit(): void {
    this.loadDashboardData();
  }

  loadDashboardData(): void {
    this.loading = true;
    this.error = '';

    // If backend expects today's date
    const today = new Date().toISOString().split('T')[0];

    this.http.get<any>(`${this.baseUrl}/dashboard?date=${today}`)
      .subscribe({
        next: (data) => {

          console.log("Dashboard API Response:", data);

          this.wardenName = data?.wardenName || 'Warden';

          this.summaryCards = [
            {
              title: 'Total Students',
              value: data?.totalStudents ?? 0,
              icon: '👨‍🎓',
              color: '#3498db'
            },
            {
              title: 'Leaves Pending',
              value: data?.pendingLeaves ?? 0,
              icon: '📄',
              color: '#f39c12'
            },
            {
              title: 'Complaints',
              value: data?.complaints ?? 0,
              icon: '⚠️',
              color: '#e74c3c'
            },
            {
              title: 'Present Today',
              value: data?.presentToday ?? 0,
              icon: '✅',
              color: '#2ecc71'
            },
            {
              title: 'Absent Today',
              value: data?.absentToday ?? 0,
              icon: '❌',
              color: '#e74c3c'
            }
          ];

          this.loading = false;
        },
        error: (err) => {
          console.error('Dashboard Load Error:', err);
          this.error = '⚠️ Failed to load dashboard data.';
          this.loading = false;
        }
      });
  }

  goToAttendance(): void {
    this.router.navigate(['/warden/mark-attendance']);
  }

  goToLeaveApproval(): void {
    this.router.navigate(['/warden/leave-approval']);
  }

  goToComplaints(): void {
    this.router.navigate(['/warden/complaints']);
  }

  logout(): void {
    this.auth.logout();
    this.router.navigate(['/login']);
  }
}
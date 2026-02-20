import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { AuthService} from '../../services/auth.service';

@Component({
  selector: 'app-mark-attendance',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './mark-attendance.html',
  styleUrls: ['./mark-attendance.css']
})
export class MarkAttendance implements OnInit {

  students: any[] = [];
  attendanceData: { [id: number]: 'PRESENT' | 'ABSENT' } = {};

  todayDate = new Date().toDateString();

  totalStudents = 0;
  presentCount = 0;
  absentCount = 0;

  loading = true;
  error = '';
  message = '';

  private readonly baseUrl = 'http://localhost:8080/api';

  constructor(
    private http: HttpClient,
    private router: Router,
    private auth: AuthService
  ) {}

  ngOnInit(): void {
    this.loadStudents();
  }

  /* ✅ Load students */
  loadStudents(): void {
    this.http.get<any[]>(`${this.baseUrl}/users/students`).subscribe({
      next: res => {
        this.students = res;
        this.totalStudents = res.length;
        this.loading = false;
      },
      error: () => {
        this.error = '❌ Failed to load students.';
        this.loading = false;
      }
    });
  }

  /* ✅ Get status */
  getStatus(studentId: number): string {
    return this.attendanceData[studentId] || 'Not Marked';
  }

  /* ✅ Mark attendance */
  mark(studentId: number, status: 'PRESENT' | 'ABSENT'): void {
    this.attendanceData[studentId] = status;

    this.presentCount = Object.values(this.attendanceData)
      .filter(s => s === 'PRESENT').length;

    this.absentCount = Object.values(this.attendanceData)
      .filter(s => s === 'ABSENT').length;
  }

  /* ✅ Submit attendance */
  submitAttendance(): void {
    const payload = Object.entries(this.attendanceData).map(([id, status]) => ({
      studentId: Number(id),
      status
    }));

    if (payload.length === 0) {
      this.error = '⚠️ Please mark attendance first.';
      return;
    }

    this.http.post(`${this.baseUrl}/warden/attendance/mark`, payload).subscribe({
      next: () => {
        this.message = '✅ Attendance submitted successfully!';
        this.error = '';
        this.attendanceData = {};
        this.presentCount = 0;
        this.absentCount = 0;
      },
      error: () => {
        this.error = '❌ Failed to submit attendance.';
      }
    });
  }

  /* ✅ Logout */
  logout(): void {
    this.auth.logout();
    this.router.navigate(['/login']);
  }
}

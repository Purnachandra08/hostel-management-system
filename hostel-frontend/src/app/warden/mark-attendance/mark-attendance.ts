import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-mark-attendance',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './mark-attendance.html',
  styleUrls: ['./mark-attendance.css']
})
export class MarkAttendance implements OnInit {

  students: any[] = [];
  previousAttendance: any[] = [];

  attendanceData: { [id: number]: 'PRESENT' | 'ABSENT' } = {};

  today = new Date();
  formattedDate = this.today.toISOString().split('T')[0];
  selectedDate = this.formattedDate;

  viewMode = false;
  loading = false;
  submitting = false;

  error = '';
  message = '';

  totalStudents = 0;
  presentCount = 0;
  absentCount = 0;

  private readonly baseUrl = 'http://localhost:8080/api';

  constructor(
    private http: HttpClient,
    private router: Router,
    private auth: AuthService
  ) {}

  ngOnInit(): void {
    this.loadStudentsByDate(this.selectedDate);
  }

  /* ==============================
     LOAD STUDENTS DATE-WISE
  ============================== */
  loadStudentsByDate(date: string): void {
    this.loading = true;
    this.error = '';
    this.message = '';

    this.http.get<any[]>(`${this.baseUrl}/warden/attendance/students/${date}`)
      .subscribe({
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

  /* ==============================
     ON DATE CHANGE
  ============================== */
  onDateChange(): void {
    this.viewMode = false;
    this.attendanceData = {};
    this.presentCount = 0;
    this.absentCount = 0;
    this.loadStudentsByDate(this.selectedDate);
  }

  /* ==============================
     MARK STATUS
  ============================== */
  mark(studentId: number, status: 'PRESENT' | 'ABSENT'): void {
    this.attendanceData[studentId] = status;
    this.calculateCounts();
  }

  calculateCounts(): void {
    this.presentCount = Object.values(this.attendanceData)
      .filter(s => s === 'PRESENT').length;

    this.absentCount = Object.values(this.attendanceData)
      .filter(s => s === 'ABSENT').length;
  }

  getStatus(studentId: number): string {
    return this.attendanceData[studentId] || 'Not Marked';
  }

  /* ==============================
     SUBMIT ATTENDANCE
  ============================== */
  submitAttendance(): void {

    if (this.submitting) return; // 🔒 Prevent double click

    if (this.selectedDate !== this.formattedDate) {
      this.error = '⚠️ You can only mark attendance for today.';
      return;
    }

    const payload = Object.entries(this.attendanceData).map(([id, status]) => ({
      studentId: Number(id),
      status,
      date: this.formattedDate
    }));

    if (payload.length === 0) {
      this.error = '⚠️ Please mark attendance first.';
      return;
    }

    this.submitting = true;
    this.loading = true;
    this.error = '';
    this.message = '';

    this.http.post(`${this.baseUrl}/warden/attendance/mark`, payload)
      .subscribe({
        next: (res: any) => {
          this.message = res.message || '✅ Attendance submitted successfully!';
          this.attendanceData = {};
          this.presentCount = 0;
          this.absentCount = 0;
          this.submitting = false;
          this.loading = false;
        },
        error: (err) => {
          this.error = err?.error?.error || '❌ Failed to submit attendance.';
          this.message = '';
          this.submitting = false;
          this.loading = false;
        }
      });
  }

  /* ==============================
     VIEW ATTENDANCE
  ============================== */
  viewAttendance(): void {

    if (!this.selectedDate) {
      this.error = '⚠️ Please select a date.';
      return;
    }

    this.loading = true;
    this.viewMode = true;
    this.message = '';
    this.error = '';

    this.http.get<any[]>(`${this.baseUrl}/warden/attendance/${this.selectedDate}`)
      .subscribe({
        next: res => {
          this.previousAttendance = res;
          this.loading = false;

          if (res.length === 0) {
            this.error = '⚠️ No attendance found for selected date.';
          }
        },
        error: () => {
          this.error = '❌ Failed to fetch attendance.';
          this.loading = false;
        }
      });
  }

  /* ==============================
     BACK TO MARK MODE
  ============================== */
  backToMark(): void {
    this.viewMode = false;
    this.previousAttendance = [];
    this.error = '';
    this.message = '';
  }

  /* ==============================
     LOGOUT
  ============================== */
  logout(): void {
    this.auth.logout();
    this.router.navigate(['/login']);
  }
}
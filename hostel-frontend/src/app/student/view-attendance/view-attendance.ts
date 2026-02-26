import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AttendanceService } from '../../services/attendance.service';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-view-attendance',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './view-attendance.html',
  styleUrls: ['./view-attendance.css']
})
export class ViewAttendance implements OnInit {

  attendanceList: any[] = [];
  studentId!: number;

  constructor(
    public auth: AuthService,
    public router: Router,
    private attendanceService: AttendanceService
  ) { }

  ngOnInit(): void {

    // ✅ Get logged-in user from sessionStorage
    const user = this.auth.getUser();

    if (!user || !user.id) {
      console.error('User not found in session');
      this.router.navigate(['/login']);
      return;
    }

    this.studentId = user.id;

    console.log("Logged in Student ID:", this.studentId); // for debugging

    this.loadAttendance();
  }

  loadAttendance() {
    this.attendanceService.getStudentAttendance(this.studentId)
      .subscribe({
        next: (data: any[]) => {

          if (!data || data.length === 0) {
            this.attendanceList = [];
            return;
          }

          // Sort by date descending
          this.attendanceList = data.sort((a, b) =>
            new Date(b.date).getTime() - new Date(a.date).getTime()
          );
        },
        error: (err) => {
          console.error('Error fetching attendance', err);
          this.attendanceList = [];
        }
      });
  }

  logout() {
    this.auth.logout();
    this.router.navigate(['/login']);
  }

}
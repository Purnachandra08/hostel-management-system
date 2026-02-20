import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-student-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './student-dashboard.html',
  styleUrls: ['./student-dashboard.css']
})
export class StudentDashboard implements OnInit {

  /* 👤 Student Info */
  studentName: string = 'Student';
  studentRoll = 'CSE2023-045';
  roomNumber = 'B-204';

  /* 🔔 Notifications */
  notifications = [
    { message: 'Warden approved your leave request', date: '2025-10-30' },
    { message: 'Room maintenance scheduled for tomorrow', date: '2025-10-31' }
  ];

  /* 🍽️ Daily Mess Menu */
  messMenu = {
    breakfast: 'Idli, Sambar, Chutney, Tea',
    lunch: 'Rice, Dal, Mixed Veg, Curd',
    dinner: 'Chapati, Paneer Curry, Rice'
  };

  constructor(
    private auth: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    const user = this.auth.getUser();

    if (user && user.username) {
      this.studentName = user.username;
    }
  }

  /* 🚪 Logout */
  logout(): void {
    this.auth.logout();
    this.router.navigate(['/login']);
  }
}

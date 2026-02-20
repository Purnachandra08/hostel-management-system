import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { UserService } from '../../services/user.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-view-profile',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './view-profile.html',
  styleUrls: ['./view-profile.css']
})
export class ViewProfile implements OnInit {
  studentName = '';
  email = '';
  course = '';
  roomNumber = '';
  phone = '';
  joinedDate = '';
  userId!: number;

  constructor(
    private router: Router,
    private userService: UserService,
    private auth: AuthService
  ) {}

  ngOnInit(): void {
    const user = this.auth.getUser();
    if (user && user.id) {
      this.userId = user.id;
      this.loadUserProfile(this.userId);
    } else {
      alert('⚠️ User not logged in');
      this.router.navigate(['/login']);
    }
  }

  // ✅ Load user data from backend
  loadUserProfile(userId: number): void {
    this.userService.getUserById(userId).subscribe({
      next: (data) => {
        this.studentName = data.username || 'Guest';
        this.email = data.email || 'N/A';
        this.course = data.course || 'Not Available';
        this.roomNumber = data.roomNumber || 'Not Assigned';
        this.phone = data.phone || 'N/A';
        this.joinedDate = data.joinedDate
          ? new Date(data.joinedDate).toLocaleDateString()
          : new Date().toLocaleDateString();
      },
      error: (err) => {
        console.error('Error fetching user profile:', err);
        alert('❌ Failed to load profile details');
      }
    });
  }

  logout(): void {
    this.auth.logout();
    this.router.navigate(['/login']);
  }
}

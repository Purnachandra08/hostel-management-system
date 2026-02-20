import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { LeaveService } from '../../services/leave.service';
import { AuthService } from '../../services/auth.service';

interface LeaveRequest {
  type: string;
  fromDate: string;
  toDate: string;
  reason: string;
}

@Component({
  selector: 'app-apply-leave',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './apply-leave.html',
  styleUrls: ['./apply-leave.css']
})
export class ApplyLeave {

  leave: LeaveRequest = {
    type: '',
    fromDate: '',
    toDate: '',
    reason: ''
  };

  message = '';
  error = '';
  isSubmitting = false;
  today = new Date().toISOString().split('T')[0];

  constructor(
    private leaveService: LeaveService,
    private authService: AuthService,
    private router: Router
  ) {}

  onSubmit() {
    this.error = '';
    this.message = '';

    // ✅ Basic validation
    if (!this.leave.type || !this.leave.fromDate || !this.leave.toDate || !this.leave.reason) {
      this.error = '⚠️ Please fill in all fields before submitting.';
      return;
    }

    // ✅ Date validation
    if (this.leave.fromDate > this.leave.toDate) {
      this.error = '⚠️ From Date cannot be after To Date.';
      return;
    }

    // ✅ Check login
    if (!this.authService.isLoggedIn()) {
      this.error = '❌ Session expired. Please login again.';
      this.router.navigate(['/login']);
      return;
    }

    this.isSubmitting = true;

    this.leaveService.applyLeave(this.leave).subscribe({
      next: (res: any) => {
        this.message = '✅ Leave applied successfully!';
        this.isSubmitting = false;

        // Reset form
        this.leave = {
          type: '',
          fromDate: '',
          toDate: '',
          reason: ''
        };

        setTimeout(() => (this.message = ''), 3000);
      },
      error: (err: any) => {
        this.error = '❌ Failed to apply leave. Please try again.';
        this.isSubmitting = false;
      }
    });
  }

  // ✅ ADDED METHOD
  goToMyLeaves() {
    this.router.navigate(['/student/my-leaves']);
  }

}
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ComplaintService } from '../../services/complaint.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-student-view-complaints',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './view-complaints.html',
  styleUrls: ['./view-complaints.css']
})
export class StudentViewComplaints implements OnInit {

  complaintsList: any[] = [];
  userId!: number;

  constructor(
    private complaintService: ComplaintService,
    private auth: AuthService
  ) {}

  ngOnInit(): void {
    const user = this.auth.getUser();
    if (user) {
      this.userId = user.id;
      this.loadComplaints();
    }
  }

  loadComplaints() {
    this.complaintService.getComplaintsByUser(this.userId).subscribe({
      next: (data) => {
        this.complaintsList = data;
      },
      error: (err) => {
        console.error('Error loading complaints:', err);
      }
    });
  }
}
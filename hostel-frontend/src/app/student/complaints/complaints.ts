import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ComplaintService } from '../../services/complaint.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-complaints',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './complaints.html',
  styleUrls: ['./complaints.css']
})
export class Complaints implements OnInit {
  complaint = { subject: '', description: '' };
  complaintsList: any[] = [];
  userId!: number;

  constructor(private complaintService: ComplaintService, private auth: AuthService) {}

  ngOnInit() {
    const user = this.auth.getUser();
    if (user) {
      this.userId = user.id;
      this.loadComplaints();
    }
  }

  // ✅ Load complaints from backend
  loadComplaints() {
    this.complaintService.getComplaintsByUser(this.userId).subscribe({
      next: (data) => (this.complaintsList = data),
      error: (err) => console.error('Error fetching complaints:', err)
    });
  }

  // ✅ Submit new complaint
  submitComplaint() {
    if (!this.complaint.subject || !this.complaint.description) {
      alert('⚠️ Please fill all fields before submitting.');
      return;
    }

    const payload = {
      subject: this.complaint.subject,
      description: this.complaint.description,
      userId: this.userId
    };

    this.complaintService.submitComplaint(payload).subscribe({
      next: () => {
        alert('✅ Complaint submitted successfully!');
        this.complaint = { subject: '', description: '' };
        this.loadComplaints(); // reload list
      },
      error: (err) => {
        console.error('Error submitting complaint:', err);
        alert('❌ Failed to submit complaint. Try again.');
      }
    });
  }
}

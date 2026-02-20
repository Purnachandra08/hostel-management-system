import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { LeaveService } from '../../services/leave.service';
import { jsPDF } from 'jspdf';
import { LeaveRequest } from '../../models/leave-request.model';

@Component({
  selector: 'app-leave-approval',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './leave-approval.html',
  styleUrls: ['./leave-approval.css']
})
export class LeaveApproval implements OnInit {
  leaveRequests: LeaveRequest[] = [];
  loading = true;
  message = '';
  error = '';

  constructor(private leaveService: LeaveService) {}

  ngOnInit(): void {
    this.loadLeaves();
  }

  // ===============================
  // Load all leaves for approval
  // ===============================
  loadLeaves(): void {
    this.loading = true;
    this.leaveService.getAllLeaves().subscribe({
      next: (data: LeaveRequest[]) => {
        this.leaveRequests = data;
        this.loading = false;
      },
      error: () => {
        this.error = '❌ Failed to load leave requests.';
        this.loading = false;
      }
    });
  }

  // ===============================
  // Approve leave and generate PDF
  // ===============================
  approve(leave: LeaveRequest): void {
    this.leaveService.approveLeave(leave.id).subscribe({
      next: () => {
        leave.status = 'APPROVED';
        this.generateGatePassPDF(leave);
        this.message = '✅ Leave approved & gate pass generated!';
        setTimeout(() => (this.message = ''), 3000);
      },
      error: () => {
        this.error = '❌ Error approving leave.';
        setTimeout(() => (this.error = ''), 3000);
      }
    });
  }

  // ===============================
  // Reject leave
  // ===============================
  reject(leave: LeaveRequest): void {
    this.leaveService.rejectLeave(leave.id).subscribe({
      next: () => {
        leave.status = 'REJECTED';
        this.message = '❌ Leave rejected successfully!';
        this.loadLeaves(); // refresh the list
        setTimeout(() => (this.message = ''), 3000);
      },
      error: () => {
        this.error = '❌ Error rejecting leave.';
        setTimeout(() => (this.error = ''), 3000);
      }
    });
  }

  // ===============================
  // Delete leave
  // ===============================
  deleteLeave(leave: LeaveRequest): void {
    if (!confirm('Are you sure you want to delete this request?')) return;

    this.leaveService.deleteLeave(leave.id).subscribe({
      next: () => {
        this.leaveRequests = this.leaveRequests.filter(l => l.id !== leave.id);
        this.message = '🗑️ Leave record deleted.';
        setTimeout(() => (this.message = ''), 3000);
      },
      error: () => {
        this.error = '❌ Failed to delete record.';
        setTimeout(() => (this.error = ''), 3000);
      }
    });
  }

  // ===============================
  // Download PDF of gate pass
  // ===============================
  downloadGatePass(leave: LeaveRequest): void {
    this.generateGatePassPDF(leave);
  }

  // ===============================
  // Generate PDF
  // ===============================
  private generateGatePassPDF(leave: LeaveRequest): void {
    const doc = new jsPDF();

    doc.setFontSize(18);
    doc.text('🏫 Hostel Gate Pass', 70, 20);

    doc.setFontSize(12);
    doc.text(`Student Name: ${leave.studentName}`, 20, 40);
    doc.text(`Room Number: ${leave.roomNumber}`, 20, 50);
    doc.text(`Leave From: ${leave.fromDate}`, 20, 60);
    doc.text(`Leave To: ${leave.toDate}`, 20, 70);
    doc.text(`Reason: ${leave.reason}`, 20, 80);
    doc.text(`Approved By: Warden`, 20, 100);
    doc.text(`Date: ${new Date().toLocaleDateString()}`, 20, 110);
    doc.text('Signature: ___________________', 20, 140);

    doc.save(`GatePass_${leave.studentName}.pdf`);
  }
}

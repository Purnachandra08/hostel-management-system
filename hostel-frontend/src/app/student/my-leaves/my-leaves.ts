import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { LeaveService } from '../../services/leave.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-my-leaves',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './my-leaves.html',
  styleUrls: ['./my-leaves.css']
})
export class MyLeaves implements OnInit {

  leaves: any[] = [];
  loading = true;

  constructor(
    private leaveService: LeaveService,
    private http: HttpClient,
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadLeaves();
  }

  loadLeaves() {
    this.leaveService.getMyLeaves().subscribe({
      next: (data: any) => {
        this.leaves = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading leaves:', err);
        this.loading = false;
      }
    });
  }

  downloadGatePass(id: number) {
    this.http.get(
      `http://localhost:8080/api/leave/download/${id}`,
      { responseType: 'blob' }
    ).subscribe({
      next: (data: Blob) => {
        const blob = new Blob([data], { type: 'application/pdf' });
        const url = window.URL.createObjectURL(blob);

        const a = document.createElement('a');
        a.href = url;
        a.download = `GatePass_${id}.pdf`;
        a.click();

        window.URL.revokeObjectURL(url);
      },
      error: (err) => {
        console.error('Gate pass download failed:', err);
        alert('Failed to download gate pass.');
      }
    });
  }

  // ✅ Added Logout Method
  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

}
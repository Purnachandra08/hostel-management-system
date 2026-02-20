import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class LeaveService {

  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  // ===============================
  // 👨‍🎓 STUDENT APIs
  // ===============================

  applyLeave(leaveData: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/api/leave/apply`, leaveData);
  }

  getMyLeaves(): Observable<any> {
    return this.http.get(`${this.apiUrl}/api/leave/my-leaves`);
  }

  // ===============================
  // 🧑‍🏫 WARDEN APIs
  // ===============================

  getAllLeaves(): Observable<any> {
    return this.http.get(`${this.apiUrl}/api/warden/leaves`);
  }

  approveLeave(id: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/api/warden/leaves/${id}/approve`, {});
  }

  rejectLeave(id: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/api/warden/leaves/${id}/reject`, {});
  }

  deleteLeave(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/api/warden/leaves/${id}`);
  }
}

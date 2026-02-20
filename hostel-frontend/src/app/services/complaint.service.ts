import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ComplaintService {
  private baseUrl = 'http://localhost:8080/api/complaints';

  constructor(private http: HttpClient) {}

  // Submit complaint ✅
  submitComplaint(complaint: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/submit`, complaint);
  }

  // Get complaints by user ✅
  getComplaintsByUser(userId: number): Observable<any> {
    return this.http.get(`${this.baseUrl}/user/${userId}`);
  }

  // Admin get all complaints ✅
  getAllComplaints(): Observable<any> {
    return this.http.get(`${this.baseUrl}/all`);
  }

  // Admin update status ✅
  updateComplaintStatus(id: number, status: string): Observable<any> {
    return this.http.put(`${this.baseUrl}/update-status/${id}?status=${status}`, {});
  }
}

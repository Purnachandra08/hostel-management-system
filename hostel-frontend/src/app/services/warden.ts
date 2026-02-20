import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class Warden {

  private baseUrl = 'http://localhost:8080/api/warden';

  constructor(private http: HttpClient) {}

  /* ===================== COMPLAINTS ===================== */

  getComplaints(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/complaints`);
  }

  approveComplaint(id: number): Observable<any> {
    return this.http.post(`${this.baseUrl}/complaints/${id}/resolve`, {});
  }

  rejectComplaint(id: number): Observable<any> {
    return this.http.post(`${this.baseUrl}/complaints/${id}/reject`, {});
  }

  /* ===================== LEAVES ===================== */

  getLeaves(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/leaves`);
  }

  approveLeave(id: number): Observable<any> {
    return this.http.post(`${this.baseUrl}/leaves/${id}/approve`, {});
  }

  rejectLeave(id: number): Observable<any> {
    return this.http.post(`${this.baseUrl}/leaves/${id}/reject`, {});
  }

  // ✅ DELETE leave request
  deleteLeave(id: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/leaves/${id}`);
  }

  /* ===================== GATE PASS PDF ===================== */

  // ✅ Download Gate Pass PDF (warden & student)
  downloadGatePass(id: number): Observable<Blob> {
    return this.http.get(`${this.baseUrl}/leaves/${id}/gate-pass`, {
      responseType: 'blob'
    });
  }
}

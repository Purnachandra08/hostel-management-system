import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AttendanceService {

  // ⚡ Base URL of your backend
  private baseUrl = 'http://localhost:8080/api/student';

  constructor(private http: HttpClient) { }

  /**
   * Get all attendance records for a student
   * @param studentId - ID of the student
   * @returns Observable<any[]> - list of attendance
   */
  getStudentAttendance(studentId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/${studentId}`);
  }
}
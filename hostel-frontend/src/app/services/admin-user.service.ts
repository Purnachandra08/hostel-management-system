import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AdminUserService {

  private readonly baseUrl = 'http://localhost:8080/api/admin/users';

  constructor(private http: HttpClient) {}

  // ===============================
  // 👑 ADMIN → CREATE WARDEN / ADMIN
  // ===============================
  createUser(data: {
    fullName: string;
    email: string;
    username: string;
    password: string;
    phone: string;
    role: 'ROLE_WARDEN' | 'ROLE_ADMIN';
  }): Observable<any> {
    return this.http.post<any>(this.baseUrl, data);
  }

  // ===============================
  // 📋 GET ALL WARDENS
  // ===============================
  getAllWardens(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/wardens`);
  }

  // ===============================
  // 🗑 DELETE WARDEN BY ID
  // ===============================
  deleteWarden(id: number): Observable<any> {
    return this.http.delete<any>(`${this.baseUrl}/${id}`);
  }
}

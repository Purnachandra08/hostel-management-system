import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class RoomService {

  private baseUrl = 'http://localhost:8080/api/rooms';

  constructor(private http: HttpClient) {}

  // ✅ Get all rooms
  getAllRooms(): Observable<any[]> {
    return this.http.get<any[]>(this.baseUrl);
  }

  // ✅ Get available rooms
  getAvailableRooms(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/available`);
  }

  // ✅ Add room
  addRoom(room: any): Observable<any> {
    return this.http.post<any>(this.baseUrl, room);
  }

  // ✅ Update room
  updateRoom(id: number, room: any): Observable<any> {
    return this.http.put(`${this.baseUrl}/${id}`, room);
  }

  // ✅ Delete room
  deleteRoom(id: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/${id}`);
  }
}
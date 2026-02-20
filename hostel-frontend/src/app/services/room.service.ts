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

  // ✅ Get only available rooms
  getAvailableRooms(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/available`);
  }

  // ✅ Add a new room
  addRoom(room: any): Observable<any> {
    return this.http.post<any>(this.baseUrl, room);
  }

  // ✅ Delete a room by ID
  deleteRoom(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}

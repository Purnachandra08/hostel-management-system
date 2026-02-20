import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class Api {

  // =========================
  // 🌐 BASE URL
  // =========================
  public readonly BASE_URL = 'http://localhost:8080/api';

  // =========================
  // 🔐 AUTH APIs
  // =========================
  public readonly AUTH = {
    REGISTER: `${this.BASE_URL}/auth/register`,     // Student register
    LOGIN: `${this.BASE_URL}/auth/login`,           // Send OTP
    VERIFY_OTP: `${this.BASE_URL}/auth/verify-otp`  // Verify OTP & get JWT
  };

  // =========================
  // 👑 ADMIN APIs
  // =========================
  public readonly ADMIN = {
    CREATE_USER: `${this.BASE_URL}/admin/users`,   // Create Warden/Admin
    GET_USERS: `${this.BASE_URL}/admin/users`,
    DELETE_USER: (id: number) =>
      `${this.BASE_URL}/admin/users/${id}`
  };

  // =========================
  // 👨‍🎓 STUDENT APIs
  // =========================
  public readonly STUDENT = {
    PROFILE: `${this.BASE_URL}/student/profile`,
    APPLY_LEAVE: `${this.BASE_URL}/student/leave`,
    MY_COMPLAINTS: `${this.BASE_URL}/student/complaints`
  };

  // =========================
  // 🧑‍🏫 WARDEN APIs
  // =========================
  public readonly WARDEN = {
    PENDING_LEAVES: `${this.BASE_URL}/warden/leaves`,
    UPDATE_LEAVE_STATUS: `${this.BASE_URL}/warden/leave/status`
  };

}

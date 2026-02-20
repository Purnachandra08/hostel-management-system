import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap, throwError } from 'rxjs';
import { environment } from '../../environments/environment';

interface AuthResponse {
  token: string;
  user: {
    id: number;
    username: string;
    email: string;
    fullName: string;
    role: string;
  };
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly baseUrl = environment.apiUrl + '/api/auth';

  private readonly tokenKey = 'authToken';
  private readonly roleKey = 'authRole';
  private readonly userKey = 'authUser';
  private readonly otpEmailKey = 'otpEmail';

  constructor(private http: HttpClient) {}

  // ===============================
  // 👨‍🎓 REGISTER
  // ===============================
  register(data: {
    fullName: string;
    email: string;
    username: string;
    password: string;
    phone: string;
  }): Observable<any> {
    return this.http.post(`${this.baseUrl}/register`, data);
  }

  // ===============================
  // 🔐 LOGIN → SEND OTP
  // ===============================
  login(username: string, password: string): Observable<any> {
    return this.http.post(`${this.baseUrl}/login`, { username, password }).pipe(
      tap((res: any) => {
        if (res?.email) {
          sessionStorage.setItem(this.otpEmailKey, res.email);
        }
      })
    );
  }

  // ===============================
  // 🔢 VERIFY OTP → RECEIVE JWT
  // ===============================
  verifyOtp(otp: string): Observable<AuthResponse> {
    const email = sessionStorage.getItem(this.otpEmailKey);

    if (!email) {
      return throwError(() => new Error('OTP email not found'));
    }

    return this.http.post<AuthResponse>(
      `${this.baseUrl}/verify-otp`,
      { email, otp }
    ).pipe(
      tap((res: AuthResponse) => {
        if (res?.token && res?.user) {
          this.storeSession(res);
        }
        sessionStorage.removeItem(this.otpEmailKey);
      })
    );
  }

  // ===============================
  // 💾 STORE SESSION
  // ===============================
  private storeSession(res: AuthResponse): void {

    sessionStorage.setItem(this.tokenKey, res.token);

    const user = res.user;

    const cleanRole = user.role.replace('ROLE_', '').toUpperCase();

    sessionStorage.setItem(this.roleKey, cleanRole);

    sessionStorage.setItem(
      this.userKey,
      JSON.stringify({
        id: user.id,
        username: user.username,
        email: user.email,
        fullName: user.fullName,
        role: cleanRole
      })
    );
  }

  // ===============================
  // 🔍 AUTH HELPERS
  // ===============================

  isLoggedIn(): boolean {
    const token = this.getToken();
    if (!token) return false;

    return !this.isTokenExpired(token);
  }

  getToken(): string | null {
    return sessionStorage.getItem(this.tokenKey);
  }

  getRole(): string | null {
    return sessionStorage.getItem(this.roleKey);
  }

  getUser(): any | null {
    const user = sessionStorage.getItem(this.userKey);
    return user ? JSON.parse(user) : null;
  }

  // ===============================
  // ⏳ TOKEN EXPIRY CHECK
  // ===============================
  private isTokenExpired(token: string): boolean {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const expiry = payload.exp;
      const now = Math.floor(new Date().getTime() / 1000);

      return expiry < now;
    } catch {
      return true;
    }
  }

  // ===============================
  // 🚪 LOGOUT
  // ===============================
  logout(): void {
    sessionStorage.clear();
  }
}

import { ApplicationConfig } from '@angular/core';
import { provideRouter, Routes, withComponentInputBinding } from '@angular/router';
import { provideHttpClient, withFetch, withInterceptors } from '@angular/common/http';

// 🌐 Public
import { LandingComponent } from './pages/landing/landing';
import { Login } from './auth/login/login';
import { Register } from './auth/register/register';
import { Otp } from './auth/otp/otp';

// 🧑‍🎓 Student
import { StudentDashboard } from './student/student-dashboard/student-dashboard';
import { ApplyLeave } from './student/apply-leave/apply-leave';
import { Complaints } from './student/complaints/complaints';
import { ViewProfile } from './student/view-profile/view-profile';
import { BookRoom } from './student/book-room/book-room';
import { MyLeaves } from './student/my-leaves/my-leaves';   // ✅ ADDED

// 🧑‍🏫 Warden
import { WardenDashboard } from './warden/warden-dashboard/warden-dashboard';
import { MarkAttendance } from './warden/mark-attendance/mark-attendance';
import { LeaveApproval } from './warden/leave-approval/leave-approval';
import { WardenComplaints } from './warden/warden-complaints/warden-complaints';

// 🧑‍💼 Admin
import { AdminDashboard } from './admin/admin-dashboard/admin-dashboard';
import { ManageRooms } from './admin/manage-rooms/manage-rooms';
import { ManageStudents } from './admin/manage-students/manage-students';
import { ManageWardens } from './admin/manage-wardens/manage-wardens';

// 🔐 Guards
import {
  authGuard,
  studentGuard,
  wardenGuard,
  adminGuard
} from './guards';

// 🔐 JWT Interceptor
import { jwtInterceptor } from './services/jwt.interceptor';

// ===============================
// ✅ ROUTES
// ===============================
export const routes: Routes = [

  // 🌐 Public
  { path: '', component: LandingComponent },
  { path: 'landing', component: LandingComponent },
  { path: 'login', component: Login },
  { path: 'register', component: Register },
  { path: 'otp', component: Otp },

  // 🧑‍🎓 Student
  {
    path: 'student',
    canActivate: [authGuard, studentGuard],
    children: [
      { path: '', component: StudentDashboard },
      { path: 'apply-leave', component: ApplyLeave },
      { path: 'my-leaves', component: MyLeaves },   // ✅ ADDED ROUTE
      { path: 'complaints', component: Complaints },
      { path: 'view-profile', component: ViewProfile },
      { path: 'book-room', component: BookRoom }
    ]
  },

  // 🧑‍🏫 Warden
  {
    path: 'warden',
    canActivate: [authGuard, wardenGuard],
    children: [
      { path: '', component: WardenDashboard },
      { path: 'mark-attendance', component: MarkAttendance },
      { path: 'leave-approval', component: LeaveApproval },
      { path: 'complaints', component: WardenComplaints }
    ]
  },

  // 🧑‍💼 Admin
  {
    path: 'admin',
    canActivate: [authGuard, adminGuard],
    children: [
      { path: '', component: AdminDashboard },
      { path: 'manage-rooms', component: ManageRooms },
      { path: 'manage-students', component: ManageStudents },
      { path: 'manage-wardens', component: ManageWardens }
    ]
  },

  // ✅ Safe fallback
  { path: '**', redirectTo: '' }
];

// ===============================
// ✅ APPLICATION CONFIG
// ===============================
export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes, withComponentInputBinding()),
    provideHttpClient(
      withFetch(),
      withInterceptors([jwtInterceptor])
    )
  ]
};
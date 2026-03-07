import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-student-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './student-dashboard.html',
  styleUrls: ['./student-dashboard.css']
})
export class StudentDashboard implements OnInit {

  /* 👤 Student Info */
  studentName: string = 'Student';
  studentRoll = 'CSE2023-045';
  roomNumber = 'B-204';

  /* 🍽️ Weekly Mess Menu */
  messMenu: any = {
    Monday: {
      breakfast: 'Idli, Sambar, Chutney, Tea',
      lunch: 'Rice, Dal, Veg Curry, Salad',
      dinner: 'Chapati, Paneer Curry, Rice'
    },
    Tuesday: {
      breakfast: 'Poha, Banana, Tea',
      lunch: 'Rice, Rajma, Mixed Veg',
      dinner: 'Chapati, Chicken Curry, Rice'
    },
    Wednesday: {
      breakfast: 'Dosa, Coconut Chutney, Tea',
      lunch: 'Rice, Dal Tadka, Aloo Fry',
      dinner: 'Chapati, Egg Curry, Rice'
    },
    Thursday: {
      breakfast: 'Upma, Tea',
      lunch: 'Rice, Chole, Salad',
      dinner: 'Chapati, Veg Korma, Rice'
    },
    Friday: {
      breakfast: 'Bread, Butter, Omelette',
      lunch: 'Rice, Dal, Cabbage Fry',
      dinner: 'Chapati, Paneer Butter Masala'
    },
    Saturday: {
      breakfast: 'Puri, Aloo Sabji',
      lunch: 'Rice, Sambar, Veg Curry',
      dinner: 'Chapati, Chicken Curry'
    },
    Sunday: {
      breakfast: 'Masala Dosa, Tea',
      lunch: 'Veg Biryani, Raita',
      dinner: 'Chapati, Special Curry'
    }
  };

  /* 📅 Selected Day */
  selectedDay: string = 'Monday';

  constructor(
    private auth: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    const user = this.auth.getUser();

    if (user && user.username) {
      this.studentName = user.username;
    }
  }

  /* 📅 Change Day */
  selectDay(day: string) {
    this.selectedDay = day;
  }

  /* 🚪 Logout */
  logout(): void {
    this.auth.logout();
    this.router.navigate(['/login']);
  }
}
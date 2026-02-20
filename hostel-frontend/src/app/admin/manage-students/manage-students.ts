import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-manage-students',
  standalone: true,
  imports: [CommonModule, FormsModule], // ✅ Added both modules
  templateUrl: './manage-students.html',
  styleUrls: ['./manage-students.css']
})
export class ManageStudents implements OnInit {
  students: any[] = [];
  filteredStudents: any[] = [];
  searchTerm: string = '';

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.loadStudents();
  }

  // ✅ Load all students
  loadStudents(): void {
    this.http.get<any[]>('http://localhost:8080/api/users/students').subscribe({
      next: (data) => {
        this.students = data;
        this.filteredStudents = data;
      },
      error: (err) => console.error('Error fetching students:', err)
    });
  }

  // ✅ Search students by username or email
  searchStudents(): void {
    const term = this.searchTerm.trim().toLowerCase();
    this.filteredStudents = this.students.filter((student) =>
      student.username.toLowerCase().includes(term) ||
      student.email.toLowerCase().includes(term)
    );
  }

  // ✅ Clear the search box
  clearSearch(): void {
    this.searchTerm = '';
    this.filteredStudents = this.students;
  }

  // ✅ Delete a student
  deleteStudent(id: number): void {
    if (confirm('Are you sure you want to delete this student?')) {
      this.http.delete(`http://localhost:8080/api/users/${id}`).subscribe({
        next: () => {
          alert('Student deleted successfully!');
          this.loadStudents();
        },
        error: (err) => console.error('Error deleting student:', err)
      });
    }
  }

  // ✅ For footer year
  currentYear(): number {
    return new Date().getFullYear();
  }
}

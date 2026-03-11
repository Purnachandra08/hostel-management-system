import { Component, OnInit, ViewChild } from '@angular/core';
import { RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { BaseChartDirective } from 'ng2-charts';
import { ChartConfiguration, ChartType, Chart, registerables } from 'chart.js';

Chart.register(...registerables);

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [RouterLink, BaseChartDirective],
  templateUrl: './admin-dashboard.html',
  styleUrls: ['./admin-dashboard.css']
})

export class AdminDashboard implements OnInit {

  constructor(private http: HttpClient) {}

  @ViewChild(BaseChartDirective) chart?: BaseChartDirective;

  // =============================
  // 📊 Bar Chart
  // =============================
  public barChartType: ChartType = 'bar';

  public barChartData: ChartConfiguration<'bar'>['data'] = {
    labels: ['Students', 'Rooms', 'Wardens'],
    datasets: [
      {
        label: 'System Overview',
        data: [0, 0, 0],
        backgroundColor: ['#4CAF50', '#2196F3', '#FF9800']
      }
    ]
  };

  // =============================
  // 🥧 Pie Chart
  // =============================
  public pieChartType: ChartType = 'pie';

  public pieChartData: ChartConfiguration<'pie'>['data'] = {
    labels: ['Occupied Rooms', 'Available Rooms'],
    datasets: [
      {
        data: [0, 0],
        backgroundColor: ['#f44336', '#4CAF50']
      }
    ]
  };

  // =============================
  // 🍩 Doughnut Chart
  // =============================
  public doughnutChartType: ChartType = 'doughnut';

  public doughnutChartData: ChartConfiguration<'doughnut'>['data'] = {
    labels: ['Pending', 'Resolved'],
    datasets: [
      {
        data: [0, 0],
        backgroundColor: ['#FFC107', '#4CAF50']
      }
    ]
  };

  // =============================
  // Load API Data
  // =============================
  ngOnInit(): void {
    this.loadAnalytics();
  }

  loadAnalytics() {

    this.http.get<any>('http://localhost:8080/api/admin/analytics')
      .subscribe({

        next: (data) => {

          // BAR CHART
          this.barChartData.datasets[0].data = [
            data.students,
            data.rooms,
            data.wardens
          ];

          // PIE CHART
          this.pieChartData.datasets[0].data = [
            data.occupiedRooms,
            data.availableRooms
          ];

          // DOUGHNUT CHART
          this.doughnutChartData.datasets[0].data = [
            data.pendingComplaints,
            data.resolvedComplaints
          ];

          // Force chart refresh
          this.chart?.update();

        },

        error: (err) => {
          console.error("Analytics API Error:", err);
        }

      });

  }

}
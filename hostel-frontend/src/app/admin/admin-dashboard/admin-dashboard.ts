import { Component, OnInit, ViewChildren, QueryList } from '@angular/core';
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

  // ✅ FIX: Get ALL charts (not just one)
  @ViewChildren(BaseChartDirective) charts!: QueryList<BaseChartDirective>;

  // =============================
  // 📊 BAR CHART
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
  // 🥧 PIE CHART
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
  // 🍩 DOUGHNUT CHART
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
  // 🚀 INIT
  // =============================
  ngOnInit(): void {
    this.loadAnalytics();
  }

  // =============================
  // 📡 LOAD ANALYTICS DATA
  // =============================
  loadAnalytics(): void {

    this.http.get<any>('http://localhost:8080/api/admin/analytics')
      .subscribe({

        next: (data) => {

          // ✅ BAR CHART DATA
          this.barChartData = {
            labels: ['Students', 'Rooms', 'Wardens'],
            datasets: [
              {
                label: 'System Overview',
                data: [
                  data.students || 0,
                  data.rooms || 0,
                  data.wardens || 0
                ],
                backgroundColor: ['#4CAF50', '#2196F3', '#FF9800']
              }
            ]
          };

          // ✅ PIE CHART DATA
          this.pieChartData = {
            labels: ['Occupied Rooms', 'Available Rooms'],
            datasets: [
              {
                data: [
                  data.occupiedRooms || 0,
                  data.availableRooms || 0
                ],
                backgroundColor: ['#f44336', '#4CAF50']
              }
            ]
          };

          // ✅ DOUGHNUT CHART DATA
          this.doughnutChartData = {
            labels: ['Pending', 'Resolved'],
            datasets: [
              {
                data: [
                  data.pendingComplaints || 0,
                  data.resolvedComplaints || 0
                ],
                backgroundColor: ['#FFC107', '#4CAF50']
              }
            ]
          };

          // ✅ FIX: Update ALL charts safely
          setTimeout(() => {
            this.charts?.forEach(chart => chart.update());
          }, 100);
        },

        error: (err) => {
          console.error('❌ Analytics API Error:', err);
        }

      });
  }

}
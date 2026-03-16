import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-landing',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './landing.html',
  styleUrls: ['./landing.css']
})
export class LandingComponent implements OnInit {

  ngOnInit() {
    this.animateCounter("studentsCount", 500);
    this.animateCounter("hostelsCount", 20);
    this.animateCounter("roomsCount", 1200);
  }

  animateCounter(id: string, target: number) {

    const element = document.getElementById(id);

    if (!element) return;

    let count = 0;
    const speed = target / 80;

    const counter = setInterval(() => {

      count += speed;

      if (count >= target) {
        element.innerText = target + "+";
        clearInterval(counter);
      } else {
        element.innerText = Math.floor(count).toString();
      }

    }, 20);
  }

}
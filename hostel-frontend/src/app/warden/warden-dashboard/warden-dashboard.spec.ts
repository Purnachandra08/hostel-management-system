import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WardenDashboard } from './warden-dashboard';

describe('WardenDashboard', () => {
  let component: WardenDashboard;
  let fixture: ComponentFixture<WardenDashboard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WardenDashboard]
    })
    .compileComponents();

    fixture = TestBed.createComponent(WardenDashboard);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

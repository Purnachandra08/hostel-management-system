import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Api } from './api';

describe('Api Service', () => {
  let service: Api;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [Api]
    });
    service = TestBed.inject(Api);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  // ✅ Optional: Add example test for mock data
  it('should return mock pending leaves', () => {
    const result = service.getMockPendingLeaves();
    expect(result.length).toBeGreaterThan(0);
  });
});

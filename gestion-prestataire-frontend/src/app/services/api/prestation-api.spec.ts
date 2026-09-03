import { TestBed } from '@angular/core/testing';

import { PrestationApiService } from './prestation-api';

describe('PrestationApiService', () => {
  let service: PrestationApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PrestationApiService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

import { TestBed } from '@angular/core/testing';

import { DevisApiService } from './devis-api';

describe('DevisApi', () => {
  let service: DevisApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DevisApiService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

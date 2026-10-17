import { TestBed } from '@angular/core/testing';

import { FactureApiService } from './facture-api';

describe('FactureApi', () => {
  let service: FactureApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FactureApiService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

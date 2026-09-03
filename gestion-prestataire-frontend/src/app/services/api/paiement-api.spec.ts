import { TestBed } from '@angular/core/testing';

import { PaiementApiService } from './paiement-api';

describe('PaiementApiService', () => {
  let service: PaiementApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PaiementApiService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

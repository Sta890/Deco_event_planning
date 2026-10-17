import { TestBed } from '@angular/core/testing';

import { PanierApiService } from './panier-api';

describe('PanierApiService', () => {
  let service: PanierApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PanierApiService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

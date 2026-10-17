import { TestBed } from '@angular/core/testing';

import { CatalogueApiService } from './catalogue-api';

describe('CatalogueApiService', () => {
  let service: CatalogueApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CatalogueApiService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

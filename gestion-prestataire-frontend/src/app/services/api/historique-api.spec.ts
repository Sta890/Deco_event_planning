import { TestBed } from '@angular/core/testing';

import { HistoriqueApiService } from './historique-api';

describe('HistoriqueApiService', () => {
  let service: HistoriqueApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(HistoriqueApiService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

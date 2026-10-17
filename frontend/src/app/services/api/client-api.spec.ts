import { TestBed } from '@angular/core/testing';

import { ClientApiService } from './client-api';

describe('ClientApi', () => {
  let service: ClientApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ClientApiService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

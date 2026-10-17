import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PrestationsComponent } from './prestations';

describe('Prestations', () => {
  let component: PrestationsComponent;
  let fixture: ComponentFixture<PrestationsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PrestationsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PrestationsComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

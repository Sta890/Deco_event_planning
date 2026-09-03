import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DevisComponent } from './devis';

describe('Devis', () => {
  let component: DevisComponent;
  let fixture: ComponentFixture<DevisComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DevisComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DevisComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

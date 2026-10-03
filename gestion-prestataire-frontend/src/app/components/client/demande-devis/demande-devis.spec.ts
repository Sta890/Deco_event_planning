import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { DemandeDevisComponent } from './demande-devis';

describe('DemandeDevisComponent', () => {
  let component: DemandeDevisComponent;
  let fixture: ComponentFixture<DemandeDevisComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DemandeDevisComponent],
      providers: [provideRouter([])]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DemandeDevisComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

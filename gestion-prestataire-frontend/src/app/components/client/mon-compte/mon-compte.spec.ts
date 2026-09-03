import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MonCompteComponent } from './mon-compte';

describe('MonCompteComponent', () => {
  let component: MonCompteComponent;
  let fixture: ComponentFixture<MonCompteComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MonCompteComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MonCompteComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { ClientLayoutComponent } from './layout';

describe('ClientLayoutComponent', () => {
  let component: ClientLayoutComponent;
  let fixture: ComponentFixture<ClientLayoutComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ClientLayoutComponent],
      providers: [provideRouter([])]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ClientLayoutComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

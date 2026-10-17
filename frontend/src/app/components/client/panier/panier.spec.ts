import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';

import { PanierComponent } from './panier';
import { AuthService } from '../../../services/auth';
import { PanierApiService, PanierResponse } from '../../../services/api/panier-api';

describe('PanierComponent', () => {
  let component: PanierComponent;
  let fixture: ComponentFixture<PanierComponent>;

  const utilisateurFactice = {
    token: 'jeton-test',
    email: 'test@exemple.cm',
    nom: 'Test',
    role: 'CLIENT',
    id: 1,
  };
  const panierVide: PanierResponse = { id: 1, utilisateurId: 1, lignes: [], total: 0 };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PanierComponent],
      providers: [
        provideRouter([]),
        {
          provide: AuthService,
          useValue: {
            getUtilisateur: () => utilisateurFactice,
            seDeconnecter: () => {},
          },
        },
        {
          provide: PanierApiService,
          useValue: {
            getPanier: () => of(panierVide),
            ajouterArticle: () => of(panierVide),
            modifierQuantite: () => of(panierVide),
            supprimerArticle: () => of(panierVide),
            viderPanier: () => of(undefined),
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(PanierComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

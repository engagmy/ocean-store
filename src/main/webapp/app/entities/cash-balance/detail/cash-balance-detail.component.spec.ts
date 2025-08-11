import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { CashBalanceDetailComponent } from './cash-balance-detail.component';

describe('CashBalance Management Detail Component', () => {
  let comp: CashBalanceDetailComponent;
  let fixture: ComponentFixture<CashBalanceDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CashBalanceDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./cash-balance-detail.component').then(m => m.CashBalanceDetailComponent),
              resolve: { cashBalance: () => of({ id: 21194 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(CashBalanceDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CashBalanceDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load cashBalance on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', CashBalanceDetailComponent);

      // THEN
      expect(instance.cashBalance()).toEqual(expect.objectContaining({ id: 21194 }));
    });
  });

  describe('PreviousState', () => {
    it('should navigate to previous state', () => {
      jest.spyOn(window.history, 'back');
      comp.previousState();
      expect(window.history.back).toHaveBeenCalled();
    });
  });
});

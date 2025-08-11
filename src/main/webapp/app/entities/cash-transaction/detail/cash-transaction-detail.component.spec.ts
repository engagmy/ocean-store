import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { CashTransactionDetailComponent } from './cash-transaction-detail.component';

describe('CashTransaction Management Detail Component', () => {
  let comp: CashTransactionDetailComponent;
  let fixture: ComponentFixture<CashTransactionDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CashTransactionDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./cash-transaction-detail.component').then(m => m.CashTransactionDetailComponent),
              resolve: { cashTransaction: () => of({ id: 22223 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(CashTransactionDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CashTransactionDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load cashTransaction on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', CashTransactionDetailComponent);

      // THEN
      expect(instance.cashTransaction()).toEqual(expect.objectContaining({ id: 22223 }));
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

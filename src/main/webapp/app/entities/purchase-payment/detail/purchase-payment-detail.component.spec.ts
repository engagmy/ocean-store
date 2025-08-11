import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { PurchasePaymentDetailComponent } from './purchase-payment-detail.component';

describe('PurchasePayment Management Detail Component', () => {
  let comp: PurchasePaymentDetailComponent;
  let fixture: ComponentFixture<PurchasePaymentDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PurchasePaymentDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./purchase-payment-detail.component').then(m => m.PurchasePaymentDetailComponent),
              resolve: { purchasePayment: () => of({ id: 18697 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(PurchasePaymentDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PurchasePaymentDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load purchasePayment on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', PurchasePaymentDetailComponent);

      // THEN
      expect(instance.purchasePayment()).toEqual(expect.objectContaining({ id: 18697 }));
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

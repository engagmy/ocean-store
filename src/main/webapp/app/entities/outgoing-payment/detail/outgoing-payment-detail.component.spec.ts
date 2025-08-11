import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { OutgoingPaymentDetailComponent } from './outgoing-payment-detail.component';

describe('OutgoingPayment Management Detail Component', () => {
  let comp: OutgoingPaymentDetailComponent;
  let fixture: ComponentFixture<OutgoingPaymentDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OutgoingPaymentDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./outgoing-payment-detail.component').then(m => m.OutgoingPaymentDetailComponent),
              resolve: { outgoingPayment: () => of({ id: 29929 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(OutgoingPaymentDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OutgoingPaymentDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load outgoingPayment on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', OutgoingPaymentDetailComponent);

      // THEN
      expect(instance.outgoingPayment()).toEqual(expect.objectContaining({ id: 29929 }));
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

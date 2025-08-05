import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { SalaryPaymentDetailComponent } from './salary-payment-detail.component';

describe('SalaryPayment Management Detail Component', () => {
  let comp: SalaryPaymentDetailComponent;
  let fixture: ComponentFixture<SalaryPaymentDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SalaryPaymentDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./salary-payment-detail.component').then(m => m.SalaryPaymentDetailComponent),
              resolve: { salaryPayment: () => of({ id: 29431 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(SalaryPaymentDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SalaryPaymentDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load salaryPayment on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', SalaryPaymentDetailComponent);

      // THEN
      expect(instance.salaryPayment()).toEqual(expect.objectContaining({ id: 29431 }));
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

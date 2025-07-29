import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { PurchaseOperationDetailComponent } from './purchase-operation-detail.component';

describe('PurchaseOperation Management Detail Component', () => {
  let comp: PurchaseOperationDetailComponent;
  let fixture: ComponentFixture<PurchaseOperationDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PurchaseOperationDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./purchase-operation-detail.component').then(m => m.PurchaseOperationDetailComponent),
              resolve: { purchaseOperation: () => of({ id: 5527 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(PurchaseOperationDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PurchaseOperationDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load purchaseOperation on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', PurchaseOperationDetailComponent);

      // THEN
      expect(instance.purchaseOperation()).toEqual(expect.objectContaining({ id: 5527 }));
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

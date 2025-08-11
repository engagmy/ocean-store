import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { DailyCashReconciliationDetailComponent } from './daily-cash-reconciliation-detail.component';

describe('DailyCashReconciliation Management Detail Component', () => {
  let comp: DailyCashReconciliationDetailComponent;
  let fixture: ComponentFixture<DailyCashReconciliationDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DailyCashReconciliationDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () =>
                import('./daily-cash-reconciliation-detail.component').then(m => m.DailyCashReconciliationDetailComponent),
              resolve: { dailyCashReconciliation: () => of({ id: 26030 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(DailyCashReconciliationDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DailyCashReconciliationDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load dailyCashReconciliation on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', DailyCashReconciliationDetailComponent);

      // THEN
      expect(instance.dailyCashReconciliation()).toEqual(expect.objectContaining({ id: 26030 }));
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

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { InventoryTransactionDetailComponent } from './inventory-transaction-detail.component';

describe('InventoryTransaction Management Detail Component', () => {
  let comp: InventoryTransactionDetailComponent;
  let fixture: ComponentFixture<InventoryTransactionDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InventoryTransactionDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./inventory-transaction-detail.component').then(m => m.InventoryTransactionDetailComponent),
              resolve: { inventoryTransaction: () => of({ id: 9318 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(InventoryTransactionDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(InventoryTransactionDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load inventoryTransaction on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', InventoryTransactionDetailComponent);

      // THEN
      expect(instance.inventoryTransaction()).toEqual(expect.objectContaining({ id: 9318 }));
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

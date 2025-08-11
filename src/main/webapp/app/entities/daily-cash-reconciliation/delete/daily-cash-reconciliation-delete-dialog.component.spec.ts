jest.mock('@ng-bootstrap/ng-bootstrap');

import { ComponentFixture, TestBed, fakeAsync, inject, tick } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { of } from 'rxjs';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { DailyCashReconciliationService } from '../service/daily-cash-reconciliation.service';

import { DailyCashReconciliationDeleteDialogComponent } from './daily-cash-reconciliation-delete-dialog.component';

describe('DailyCashReconciliation Management Delete Component', () => {
  let comp: DailyCashReconciliationDeleteDialogComponent;
  let fixture: ComponentFixture<DailyCashReconciliationDeleteDialogComponent>;
  let service: DailyCashReconciliationService;
  let mockActiveModal: NgbActiveModal;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [DailyCashReconciliationDeleteDialogComponent],
      providers: [provideHttpClient(), NgbActiveModal],
    })
      .overrideTemplate(DailyCashReconciliationDeleteDialogComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(DailyCashReconciliationDeleteDialogComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(DailyCashReconciliationService);
    mockActiveModal = TestBed.inject(NgbActiveModal);
  });

  describe('confirmDelete', () => {
    it('should call delete service on confirmDelete', inject(
      [],
      fakeAsync(() => {
        // GIVEN
        jest.spyOn(service, 'delete').mockReturnValue(of(new HttpResponse({ body: {} })));

        // WHEN
        comp.confirmDelete(123);
        tick();

        // THEN
        expect(service.delete).toHaveBeenCalledWith(123);
        expect(mockActiveModal.close).toHaveBeenCalledWith('deleted');
      }),
    ));

    it('should not call delete service on clear', () => {
      // GIVEN
      jest.spyOn(service, 'delete');

      // WHEN
      comp.cancel();

      // THEN
      expect(service.delete).not.toHaveBeenCalled();
      expect(mockActiveModal.close).not.toHaveBeenCalled();
      expect(mockActiveModal.dismiss).toHaveBeenCalled();
    });
  });
});

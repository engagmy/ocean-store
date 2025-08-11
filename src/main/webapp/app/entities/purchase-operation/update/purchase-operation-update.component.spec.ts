import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IBill } from 'app/entities/bill/bill.model';
import { BillService } from 'app/entities/bill/service/bill.service';
import { PurchaseOperationService } from '../service/purchase-operation.service';
import { IPurchaseOperation } from '../purchase-operation.model';
import { PurchaseOperationFormService } from './purchase-operation-form.service';

import { PurchaseOperationUpdateComponent } from './purchase-operation-update.component';

describe('PurchaseOperation Management Update Component', () => {
  let comp: PurchaseOperationUpdateComponent;
  let fixture: ComponentFixture<PurchaseOperationUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let purchaseOperationFormService: PurchaseOperationFormService;
  let purchaseOperationService: PurchaseOperationService;
  let billService: BillService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [PurchaseOperationUpdateComponent],
      providers: [
        provideHttpClient(),
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(PurchaseOperationUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PurchaseOperationUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    purchaseOperationFormService = TestBed.inject(PurchaseOperationFormService);
    purchaseOperationService = TestBed.inject(PurchaseOperationService);
    billService = TestBed.inject(BillService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Bill query and add missing value', () => {
      const purchaseOperation: IPurchaseOperation = { id: 30521 };
      const bill: IBill = { id: 8530 };
      purchaseOperation.bill = bill;

      const billCollection: IBill[] = [{ id: 8530 }];
      jest.spyOn(billService, 'query').mockReturnValue(of(new HttpResponse({ body: billCollection })));
      const additionalBills = [bill];
      const expectedCollection: IBill[] = [...additionalBills, ...billCollection];
      jest.spyOn(billService, 'addBillToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ purchaseOperation });
      comp.ngOnInit();

      expect(billService.query).toHaveBeenCalled();
      expect(billService.addBillToCollectionIfMissing).toHaveBeenCalledWith(
        billCollection,
        ...additionalBills.map(expect.objectContaining),
      );
      expect(comp.billsSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const purchaseOperation: IPurchaseOperation = { id: 30521 };
      const bill: IBill = { id: 8530 };
      purchaseOperation.bill = bill;

      activatedRoute.data = of({ purchaseOperation });
      comp.ngOnInit();

      expect(comp.billsSharedCollection).toContainEqual(bill);
      expect(comp.purchaseOperation).toEqual(purchaseOperation);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPurchaseOperation>>();
      const purchaseOperation = { id: 5527 };
      jest.spyOn(purchaseOperationFormService, 'getPurchaseOperation').mockReturnValue(purchaseOperation);
      jest.spyOn(purchaseOperationService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ purchaseOperation });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: purchaseOperation }));
      saveSubject.complete();

      // THEN
      expect(purchaseOperationFormService.getPurchaseOperation).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(purchaseOperationService.update).toHaveBeenCalledWith(expect.objectContaining(purchaseOperation));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPurchaseOperation>>();
      const purchaseOperation = { id: 5527 };
      jest.spyOn(purchaseOperationFormService, 'getPurchaseOperation').mockReturnValue({ id: null });
      jest.spyOn(purchaseOperationService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ purchaseOperation: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: purchaseOperation }));
      saveSubject.complete();

      // THEN
      expect(purchaseOperationFormService.getPurchaseOperation).toHaveBeenCalled();
      expect(purchaseOperationService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPurchaseOperation>>();
      const purchaseOperation = { id: 5527 };
      jest.spyOn(purchaseOperationService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ purchaseOperation });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(purchaseOperationService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareBill', () => {
      it('should forward to billService', () => {
        const entity = { id: 8530 };
        const entity2 = { id: 14455 };
        jest.spyOn(billService, 'compareBill');
        comp.compareBill(entity, entity2);
        expect(billService.compareBill).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IBill } from 'app/entities/bill/bill.model';
import { BillService } from 'app/entities/bill/service/bill.service';
import { ICustomer } from 'app/entities/customer/customer.model';
import { CustomerService } from 'app/entities/customer/service/customer.service';
import { ISaleOperation } from '../sale-operation.model';
import { SaleOperationService } from '../service/sale-operation.service';
import { SaleOperationFormService } from './sale-operation-form.service';

import { SaleOperationUpdateComponent } from './sale-operation-update.component';

describe('SaleOperation Management Update Component', () => {
  let comp: SaleOperationUpdateComponent;
  let fixture: ComponentFixture<SaleOperationUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let saleOperationFormService: SaleOperationFormService;
  let saleOperationService: SaleOperationService;
  let billService: BillService;
  let customerService: CustomerService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [SaleOperationUpdateComponent],
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
      .overrideTemplate(SaleOperationUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(SaleOperationUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    saleOperationFormService = TestBed.inject(SaleOperationFormService);
    saleOperationService = TestBed.inject(SaleOperationService);
    billService = TestBed.inject(BillService);
    customerService = TestBed.inject(CustomerService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Bill query and add missing value', () => {
      const saleOperation: ISaleOperation = { id: 11698 };
      const bill: IBill = { id: 8530 };
      saleOperation.bill = bill;

      const billCollection: IBill[] = [{ id: 8530 }];
      jest.spyOn(billService, 'query').mockReturnValue(of(new HttpResponse({ body: billCollection })));
      const additionalBills = [bill];
      const expectedCollection: IBill[] = [...additionalBills, ...billCollection];
      jest.spyOn(billService, 'addBillToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ saleOperation });
      comp.ngOnInit();

      expect(billService.query).toHaveBeenCalled();
      expect(billService.addBillToCollectionIfMissing).toHaveBeenCalledWith(
        billCollection,
        ...additionalBills.map(expect.objectContaining),
      );
      expect(comp.billsSharedCollection).toEqual(expectedCollection);
    });

    it('should call Customer query and add missing value', () => {
      const saleOperation: ISaleOperation = { id: 11698 };
      const customer: ICustomer = { id: 26915 };
      saleOperation.customer = customer;

      const customerCollection: ICustomer[] = [{ id: 26915 }];
      jest.spyOn(customerService, 'query').mockReturnValue(of(new HttpResponse({ body: customerCollection })));
      const additionalCustomers = [customer];
      const expectedCollection: ICustomer[] = [...additionalCustomers, ...customerCollection];
      jest.spyOn(customerService, 'addCustomerToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ saleOperation });
      comp.ngOnInit();

      expect(customerService.query).toHaveBeenCalled();
      expect(customerService.addCustomerToCollectionIfMissing).toHaveBeenCalledWith(
        customerCollection,
        ...additionalCustomers.map(expect.objectContaining),
      );
      expect(comp.customersSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const saleOperation: ISaleOperation = { id: 11698 };
      const bill: IBill = { id: 8530 };
      saleOperation.bill = bill;
      const customer: ICustomer = { id: 26915 };
      saleOperation.customer = customer;

      activatedRoute.data = of({ saleOperation });
      comp.ngOnInit();

      expect(comp.billsSharedCollection).toContainEqual(bill);
      expect(comp.customersSharedCollection).toContainEqual(customer);
      expect(comp.saleOperation).toEqual(saleOperation);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISaleOperation>>();
      const saleOperation = { id: 25388 };
      jest.spyOn(saleOperationFormService, 'getSaleOperation').mockReturnValue(saleOperation);
      jest.spyOn(saleOperationService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ saleOperation });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: saleOperation }));
      saveSubject.complete();

      // THEN
      expect(saleOperationFormService.getSaleOperation).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(saleOperationService.update).toHaveBeenCalledWith(expect.objectContaining(saleOperation));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISaleOperation>>();
      const saleOperation = { id: 25388 };
      jest.spyOn(saleOperationFormService, 'getSaleOperation').mockReturnValue({ id: null });
      jest.spyOn(saleOperationService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ saleOperation: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: saleOperation }));
      saveSubject.complete();

      // THEN
      expect(saleOperationFormService.getSaleOperation).toHaveBeenCalled();
      expect(saleOperationService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISaleOperation>>();
      const saleOperation = { id: 25388 };
      jest.spyOn(saleOperationService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ saleOperation });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(saleOperationService.update).toHaveBeenCalled();
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

    describe('compareCustomer', () => {
      it('should forward to customerService', () => {
        const entity = { id: 26915 };
        const entity2 = { id: 21032 };
        jest.spyOn(customerService, 'compareCustomer');
        comp.compareCustomer(entity, entity2);
        expect(customerService.compareCustomer).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});

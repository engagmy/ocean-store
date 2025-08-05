import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { ISale } from 'app/entities/sale/sale.model';
import { SaleService } from 'app/entities/sale/service/sale.service';
import { SalePaymentService } from '../service/sale-payment.service';
import { ISalePayment } from '../sale-payment.model';
import { SalePaymentFormService } from './sale-payment-form.service';

import { SalePaymentUpdateComponent } from './sale-payment-update.component';

describe('SalePayment Management Update Component', () => {
  let comp: SalePaymentUpdateComponent;
  let fixture: ComponentFixture<SalePaymentUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let salePaymentFormService: SalePaymentFormService;
  let salePaymentService: SalePaymentService;
  let saleService: SaleService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [SalePaymentUpdateComponent],
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
      .overrideTemplate(SalePaymentUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(SalePaymentUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    salePaymentFormService = TestBed.inject(SalePaymentFormService);
    salePaymentService = TestBed.inject(SalePaymentService);
    saleService = TestBed.inject(SaleService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Sale query and add missing value', () => {
      const salePayment: ISalePayment = { id: 26899 };
      const sale: ISale = { id: 2908 };
      salePayment.sale = sale;

      const saleCollection: ISale[] = [{ id: 2908 }];
      jest.spyOn(saleService, 'query').mockReturnValue(of(new HttpResponse({ body: saleCollection })));
      const additionalSales = [sale];
      const expectedCollection: ISale[] = [...additionalSales, ...saleCollection];
      jest.spyOn(saleService, 'addSaleToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ salePayment });
      comp.ngOnInit();

      expect(saleService.query).toHaveBeenCalled();
      expect(saleService.addSaleToCollectionIfMissing).toHaveBeenCalledWith(
        saleCollection,
        ...additionalSales.map(expect.objectContaining),
      );
      expect(comp.salesSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const salePayment: ISalePayment = { id: 26899 };
      const sale: ISale = { id: 2908 };
      salePayment.sale = sale;

      activatedRoute.data = of({ salePayment });
      comp.ngOnInit();

      expect(comp.salesSharedCollection).toContainEqual(sale);
      expect(comp.salePayment).toEqual(salePayment);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISalePayment>>();
      const salePayment = { id: 11117 };
      jest.spyOn(salePaymentFormService, 'getSalePayment').mockReturnValue(salePayment);
      jest.spyOn(salePaymentService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ salePayment });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: salePayment }));
      saveSubject.complete();

      // THEN
      expect(salePaymentFormService.getSalePayment).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(salePaymentService.update).toHaveBeenCalledWith(expect.objectContaining(salePayment));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISalePayment>>();
      const salePayment = { id: 11117 };
      jest.spyOn(salePaymentFormService, 'getSalePayment').mockReturnValue({ id: null });
      jest.spyOn(salePaymentService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ salePayment: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: salePayment }));
      saveSubject.complete();

      // THEN
      expect(salePaymentFormService.getSalePayment).toHaveBeenCalled();
      expect(salePaymentService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISalePayment>>();
      const salePayment = { id: 11117 };
      jest.spyOn(salePaymentService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ salePayment });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(salePaymentService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareSale', () => {
      it('should forward to saleService', () => {
        const entity = { id: 2908 };
        const entity2 = { id: 10270 };
        jest.spyOn(saleService, 'compareSale');
        comp.compareSale(entity, entity2);
        expect(saleService.compareSale).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});

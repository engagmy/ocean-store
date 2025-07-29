import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IPurchase } from 'app/entities/purchase/purchase.model';
import { PurchaseService } from 'app/entities/purchase/service/purchase.service';
import { PurchasePaymentService } from '../service/purchase-payment.service';
import { IPurchasePayment } from '../purchase-payment.model';
import { PurchasePaymentFormService } from './purchase-payment-form.service';

import { PurchasePaymentUpdateComponent } from './purchase-payment-update.component';

describe('PurchasePayment Management Update Component', () => {
  let comp: PurchasePaymentUpdateComponent;
  let fixture: ComponentFixture<PurchasePaymentUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let purchasePaymentFormService: PurchasePaymentFormService;
  let purchasePaymentService: PurchasePaymentService;
  let purchaseService: PurchaseService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [PurchasePaymentUpdateComponent],
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
      .overrideTemplate(PurchasePaymentUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PurchasePaymentUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    purchasePaymentFormService = TestBed.inject(PurchasePaymentFormService);
    purchasePaymentService = TestBed.inject(PurchasePaymentService);
    purchaseService = TestBed.inject(PurchaseService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Purchase query and add missing value', () => {
      const purchasePayment: IPurchasePayment = { id: 7942 };
      const purchase: IPurchase = { id: 7971 };
      purchasePayment.purchase = purchase;

      const purchaseCollection: IPurchase[] = [{ id: 7971 }];
      jest.spyOn(purchaseService, 'query').mockReturnValue(of(new HttpResponse({ body: purchaseCollection })));
      const additionalPurchases = [purchase];
      const expectedCollection: IPurchase[] = [...additionalPurchases, ...purchaseCollection];
      jest.spyOn(purchaseService, 'addPurchaseToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ purchasePayment });
      comp.ngOnInit();

      expect(purchaseService.query).toHaveBeenCalled();
      expect(purchaseService.addPurchaseToCollectionIfMissing).toHaveBeenCalledWith(
        purchaseCollection,
        ...additionalPurchases.map(expect.objectContaining),
      );
      expect(comp.purchasesSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const purchasePayment: IPurchasePayment = { id: 7942 };
      const purchase: IPurchase = { id: 7971 };
      purchasePayment.purchase = purchase;

      activatedRoute.data = of({ purchasePayment });
      comp.ngOnInit();

      expect(comp.purchasesSharedCollection).toContainEqual(purchase);
      expect(comp.purchasePayment).toEqual(purchasePayment);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPurchasePayment>>();
      const purchasePayment = { id: 18697 };
      jest.spyOn(purchasePaymentFormService, 'getPurchasePayment').mockReturnValue(purchasePayment);
      jest.spyOn(purchasePaymentService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ purchasePayment });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: purchasePayment }));
      saveSubject.complete();

      // THEN
      expect(purchasePaymentFormService.getPurchasePayment).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(purchasePaymentService.update).toHaveBeenCalledWith(expect.objectContaining(purchasePayment));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPurchasePayment>>();
      const purchasePayment = { id: 18697 };
      jest.spyOn(purchasePaymentFormService, 'getPurchasePayment').mockReturnValue({ id: null });
      jest.spyOn(purchasePaymentService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ purchasePayment: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: purchasePayment }));
      saveSubject.complete();

      // THEN
      expect(purchasePaymentFormService.getPurchasePayment).toHaveBeenCalled();
      expect(purchasePaymentService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPurchasePayment>>();
      const purchasePayment = { id: 18697 };
      jest.spyOn(purchasePaymentService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ purchasePayment });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(purchasePaymentService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('comparePurchase', () => {
      it('should forward to purchaseService', () => {
        const entity = { id: 7971 };
        const entity2 = { id: 8022 };
        jest.spyOn(purchaseService, 'comparePurchase');
        comp.comparePurchase(entity, entity2);
        expect(purchaseService.comparePurchase).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});

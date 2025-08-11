import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IPurchaseOperation } from 'app/entities/purchase-operation/purchase-operation.model';
import { PurchaseOperationService } from 'app/entities/purchase-operation/service/purchase-operation.service';
import { IProduct } from 'app/entities/product/product.model';
import { ProductService } from 'app/entities/product/service/product.service';
import { ISupplier } from 'app/entities/supplier/supplier.model';
import { SupplierService } from 'app/entities/supplier/service/supplier.service';
import { IPurchase } from '../purchase.model';
import { PurchaseService } from '../service/purchase.service';
import { PurchaseFormService } from './purchase-form.service';

import { PurchaseUpdateComponent } from './purchase-update.component';

describe('Purchase Management Update Component', () => {
  let comp: PurchaseUpdateComponent;
  let fixture: ComponentFixture<PurchaseUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let purchaseFormService: PurchaseFormService;
  let purchaseService: PurchaseService;
  let purchaseOperationService: PurchaseOperationService;
  let productService: ProductService;
  let supplierService: SupplierService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [PurchaseUpdateComponent],
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
      .overrideTemplate(PurchaseUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PurchaseUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    purchaseFormService = TestBed.inject(PurchaseFormService);
    purchaseService = TestBed.inject(PurchaseService);
    purchaseOperationService = TestBed.inject(PurchaseOperationService);
    productService = TestBed.inject(ProductService);
    supplierService = TestBed.inject(SupplierService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call PurchaseOperation query and add missing value', () => {
      const purchase: IPurchase = { id: 8022 };
      const purchaseOperation: IPurchaseOperation = { id: 5527 };
      purchase.purchaseOperation = purchaseOperation;

      const purchaseOperationCollection: IPurchaseOperation[] = [{ id: 5527 }];
      jest.spyOn(purchaseOperationService, 'query').mockReturnValue(of(new HttpResponse({ body: purchaseOperationCollection })));
      const additionalPurchaseOperations = [purchaseOperation];
      const expectedCollection: IPurchaseOperation[] = [...additionalPurchaseOperations, ...purchaseOperationCollection];
      jest.spyOn(purchaseOperationService, 'addPurchaseOperationToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ purchase });
      comp.ngOnInit();

      expect(purchaseOperationService.query).toHaveBeenCalled();
      expect(purchaseOperationService.addPurchaseOperationToCollectionIfMissing).toHaveBeenCalledWith(
        purchaseOperationCollection,
        ...additionalPurchaseOperations.map(expect.objectContaining),
      );
      expect(comp.purchaseOperationsSharedCollection).toEqual(expectedCollection);
    });

    it('should call Product query and add missing value', () => {
      const purchase: IPurchase = { id: 8022 };
      const product: IProduct = { id: 21536 };
      purchase.product = product;

      const productCollection: IProduct[] = [{ id: 21536 }];
      jest.spyOn(productService, 'query').mockReturnValue(of(new HttpResponse({ body: productCollection })));
      const additionalProducts = [product];
      const expectedCollection: IProduct[] = [...additionalProducts, ...productCollection];
      jest.spyOn(productService, 'addProductToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ purchase });
      comp.ngOnInit();

      expect(productService.query).toHaveBeenCalled();
      expect(productService.addProductToCollectionIfMissing).toHaveBeenCalledWith(
        productCollection,
        ...additionalProducts.map(expect.objectContaining),
      );
      expect(comp.productsSharedCollection).toEqual(expectedCollection);
    });

    it('should call Supplier query and add missing value', () => {
      const purchase: IPurchase = { id: 8022 };
      const supplier: ISupplier = { id: 28889 };
      purchase.supplier = supplier;

      const supplierCollection: ISupplier[] = [{ id: 28889 }];
      jest.spyOn(supplierService, 'query').mockReturnValue(of(new HttpResponse({ body: supplierCollection })));
      const additionalSuppliers = [supplier];
      const expectedCollection: ISupplier[] = [...additionalSuppliers, ...supplierCollection];
      jest.spyOn(supplierService, 'addSupplierToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ purchase });
      comp.ngOnInit();

      expect(supplierService.query).toHaveBeenCalled();
      expect(supplierService.addSupplierToCollectionIfMissing).toHaveBeenCalledWith(
        supplierCollection,
        ...additionalSuppliers.map(expect.objectContaining),
      );
      expect(comp.suppliersSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const purchase: IPurchase = { id: 8022 };
      const purchaseOperation: IPurchaseOperation = { id: 5527 };
      purchase.purchaseOperation = purchaseOperation;
      const product: IProduct = { id: 21536 };
      purchase.product = product;
      const supplier: ISupplier = { id: 28889 };
      purchase.supplier = supplier;

      activatedRoute.data = of({ purchase });
      comp.ngOnInit();

      expect(comp.purchaseOperationsSharedCollection).toContainEqual(purchaseOperation);
      expect(comp.productsSharedCollection).toContainEqual(product);
      expect(comp.suppliersSharedCollection).toContainEqual(supplier);
      expect(comp.purchase).toEqual(purchase);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPurchase>>();
      const purchase = { id: 7971 };
      jest.spyOn(purchaseFormService, 'getPurchase').mockReturnValue(purchase);
      jest.spyOn(purchaseService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ purchase });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: purchase }));
      saveSubject.complete();

      // THEN
      expect(purchaseFormService.getPurchase).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(purchaseService.update).toHaveBeenCalledWith(expect.objectContaining(purchase));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPurchase>>();
      const purchase = { id: 7971 };
      jest.spyOn(purchaseFormService, 'getPurchase').mockReturnValue({ id: null });
      jest.spyOn(purchaseService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ purchase: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: purchase }));
      saveSubject.complete();

      // THEN
      expect(purchaseFormService.getPurchase).toHaveBeenCalled();
      expect(purchaseService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPurchase>>();
      const purchase = { id: 7971 };
      jest.spyOn(purchaseService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ purchase });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(purchaseService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('comparePurchaseOperation', () => {
      it('should forward to purchaseOperationService', () => {
        const entity = { id: 5527 };
        const entity2 = { id: 30521 };
        jest.spyOn(purchaseOperationService, 'comparePurchaseOperation');
        comp.comparePurchaseOperation(entity, entity2);
        expect(purchaseOperationService.comparePurchaseOperation).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareProduct', () => {
      it('should forward to productService', () => {
        const entity = { id: 21536 };
        const entity2 = { id: 11926 };
        jest.spyOn(productService, 'compareProduct');
        comp.compareProduct(entity, entity2);
        expect(productService.compareProduct).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareSupplier', () => {
      it('should forward to supplierService', () => {
        const entity = { id: 28889 };
        const entity2 = { id: 5063 };
        jest.spyOn(supplierService, 'compareSupplier');
        comp.compareSupplier(entity, entity2);
        expect(supplierService.compareSupplier).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});

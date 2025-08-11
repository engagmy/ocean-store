import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IProduct } from 'app/entities/product/product.model';
import { ProductService } from 'app/entities/product/service/product.service';
import { InventoryTransactionService } from '../service/inventory-transaction.service';
import { IInventoryTransaction } from '../inventory-transaction.model';
import { InventoryTransactionFormService } from './inventory-transaction-form.service';

import { InventoryTransactionUpdateComponent } from './inventory-transaction-update.component';

describe('InventoryTransaction Management Update Component', () => {
  let comp: InventoryTransactionUpdateComponent;
  let fixture: ComponentFixture<InventoryTransactionUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let inventoryTransactionFormService: InventoryTransactionFormService;
  let inventoryTransactionService: InventoryTransactionService;
  let productService: ProductService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [InventoryTransactionUpdateComponent],
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
      .overrideTemplate(InventoryTransactionUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(InventoryTransactionUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    inventoryTransactionFormService = TestBed.inject(InventoryTransactionFormService);
    inventoryTransactionService = TestBed.inject(InventoryTransactionService);
    productService = TestBed.inject(ProductService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Product query and add missing value', () => {
      const inventoryTransaction: IInventoryTransaction = { id: 2881 };
      const product: IProduct = { id: 21536 };
      inventoryTransaction.product = product;

      const productCollection: IProduct[] = [{ id: 21536 }];
      jest.spyOn(productService, 'query').mockReturnValue(of(new HttpResponse({ body: productCollection })));
      const additionalProducts = [product];
      const expectedCollection: IProduct[] = [...additionalProducts, ...productCollection];
      jest.spyOn(productService, 'addProductToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ inventoryTransaction });
      comp.ngOnInit();

      expect(productService.query).toHaveBeenCalled();
      expect(productService.addProductToCollectionIfMissing).toHaveBeenCalledWith(
        productCollection,
        ...additionalProducts.map(expect.objectContaining),
      );
      expect(comp.productsSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const inventoryTransaction: IInventoryTransaction = { id: 2881 };
      const product: IProduct = { id: 21536 };
      inventoryTransaction.product = product;

      activatedRoute.data = of({ inventoryTransaction });
      comp.ngOnInit();

      expect(comp.productsSharedCollection).toContainEqual(product);
      expect(comp.inventoryTransaction).toEqual(inventoryTransaction);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IInventoryTransaction>>();
      const inventoryTransaction = { id: 9318 };
      jest.spyOn(inventoryTransactionFormService, 'getInventoryTransaction').mockReturnValue(inventoryTransaction);
      jest.spyOn(inventoryTransactionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ inventoryTransaction });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: inventoryTransaction }));
      saveSubject.complete();

      // THEN
      expect(inventoryTransactionFormService.getInventoryTransaction).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(inventoryTransactionService.update).toHaveBeenCalledWith(expect.objectContaining(inventoryTransaction));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IInventoryTransaction>>();
      const inventoryTransaction = { id: 9318 };
      jest.spyOn(inventoryTransactionFormService, 'getInventoryTransaction').mockReturnValue({ id: null });
      jest.spyOn(inventoryTransactionService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ inventoryTransaction: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: inventoryTransaction }));
      saveSubject.complete();

      // THEN
      expect(inventoryTransactionFormService.getInventoryTransaction).toHaveBeenCalled();
      expect(inventoryTransactionService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IInventoryTransaction>>();
      const inventoryTransaction = { id: 9318 };
      jest.spyOn(inventoryTransactionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ inventoryTransaction });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(inventoryTransactionService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareProduct', () => {
      it('should forward to productService', () => {
        const entity = { id: 21536 };
        const entity2 = { id: 11926 };
        jest.spyOn(productService, 'compareProduct');
        comp.compareProduct(entity, entity2);
        expect(productService.compareProduct).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});

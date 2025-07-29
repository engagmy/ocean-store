import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IPurchaseOperation } from 'app/entities/purchase-operation/purchase-operation.model';
import { PurchaseOperationService } from 'app/entities/purchase-operation/service/purchase-operation.service';
import { IProduct } from 'app/entities/product/product.model';
import { ProductService } from 'app/entities/product/service/product.service';
import { ISupplier } from 'app/entities/supplier/supplier.model';
import { SupplierService } from 'app/entities/supplier/service/supplier.service';
import { PurchaseService } from '../service/purchase.service';
import { IPurchase } from '../purchase.model';
import { PurchaseFormGroup, PurchaseFormService } from './purchase-form.service';

@Component({
  selector: 'jhi-purchase-update',
  templateUrl: './purchase-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class PurchaseUpdateComponent implements OnInit {
  isSaving = false;
  purchase: IPurchase | null = null;

  purchaseOperationsSharedCollection: IPurchaseOperation[] = [];
  productsSharedCollection: IProduct[] = [];
  suppliersSharedCollection: ISupplier[] = [];

  protected purchaseService = inject(PurchaseService);
  protected purchaseFormService = inject(PurchaseFormService);
  protected purchaseOperationService = inject(PurchaseOperationService);
  protected productService = inject(ProductService);
  protected supplierService = inject(SupplierService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: PurchaseFormGroup = this.purchaseFormService.createPurchaseFormGroup();

  comparePurchaseOperation = (o1: IPurchaseOperation | null, o2: IPurchaseOperation | null): boolean =>
    this.purchaseOperationService.comparePurchaseOperation(o1, o2);

  compareProduct = (o1: IProduct | null, o2: IProduct | null): boolean => this.productService.compareProduct(o1, o2);

  compareSupplier = (o1: ISupplier | null, o2: ISupplier | null): boolean => this.supplierService.compareSupplier(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ purchase }) => {
      this.purchase = purchase;
      if (purchase) {
        this.updateForm(purchase);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const purchase = this.purchaseFormService.getPurchase(this.editForm);
    if (purchase.id !== null) {
      this.subscribeToSaveResponse(this.purchaseService.update(purchase));
    } else {
      this.subscribeToSaveResponse(this.purchaseService.create(purchase));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPurchase>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(purchase: IPurchase): void {
    this.purchase = purchase;
    this.purchaseFormService.resetForm(this.editForm, purchase);

    this.purchaseOperationsSharedCollection = this.purchaseOperationService.addPurchaseOperationToCollectionIfMissing<IPurchaseOperation>(
      this.purchaseOperationsSharedCollection,
      purchase.purchaseOperation,
    );
    this.productsSharedCollection = this.productService.addProductToCollectionIfMissing<IProduct>(
      this.productsSharedCollection,
      purchase.product,
    );
    this.suppliersSharedCollection = this.supplierService.addSupplierToCollectionIfMissing<ISupplier>(
      this.suppliersSharedCollection,
      purchase.supplier,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.purchaseOperationService
      .query()
      .pipe(map((res: HttpResponse<IPurchaseOperation[]>) => res.body ?? []))
      .pipe(
        map((purchaseOperations: IPurchaseOperation[]) =>
          this.purchaseOperationService.addPurchaseOperationToCollectionIfMissing<IPurchaseOperation>(
            purchaseOperations,
            this.purchase?.purchaseOperation,
          ),
        ),
      )
      .subscribe((purchaseOperations: IPurchaseOperation[]) => (this.purchaseOperationsSharedCollection = purchaseOperations));

    this.productService
      .query()
      .pipe(map((res: HttpResponse<IProduct[]>) => res.body ?? []))
      .pipe(map((products: IProduct[]) => this.productService.addProductToCollectionIfMissing<IProduct>(products, this.purchase?.product)))
      .subscribe((products: IProduct[]) => (this.productsSharedCollection = products));

    this.supplierService
      .query()
      .pipe(map((res: HttpResponse<ISupplier[]>) => res.body ?? []))
      .pipe(
        map((suppliers: ISupplier[]) =>
          this.supplierService.addSupplierToCollectionIfMissing<ISupplier>(suppliers, this.purchase?.supplier),
        ),
      )
      .subscribe((suppliers: ISupplier[]) => (this.suppliersSharedCollection = suppliers));
  }
}

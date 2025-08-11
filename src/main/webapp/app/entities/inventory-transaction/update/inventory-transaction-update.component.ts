import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IProduct } from 'app/entities/product/product.model';
import { ProductService } from 'app/entities/product/service/product.service';
import { InventoryActionType } from 'app/entities/enumerations/inventory-action-type.model';
import { InventoryTransactionService } from '../service/inventory-transaction.service';
import { IInventoryTransaction } from '../inventory-transaction.model';
import { InventoryTransactionFormGroup, InventoryTransactionFormService } from './inventory-transaction-form.service';

@Component({
  selector: 'jhi-inventory-transaction-update',
  templateUrl: './inventory-transaction-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class InventoryTransactionUpdateComponent implements OnInit {
  isSaving = false;
  inventoryTransaction: IInventoryTransaction | null = null;
  inventoryActionTypeValues = Object.keys(InventoryActionType);

  productsSharedCollection: IProduct[] = [];

  protected inventoryTransactionService = inject(InventoryTransactionService);
  protected inventoryTransactionFormService = inject(InventoryTransactionFormService);
  protected productService = inject(ProductService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: InventoryTransactionFormGroup = this.inventoryTransactionFormService.createInventoryTransactionFormGroup();

  compareProduct = (o1: IProduct | null, o2: IProduct | null): boolean => this.productService.compareProduct(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ inventoryTransaction }) => {
      this.inventoryTransaction = inventoryTransaction;
      if (inventoryTransaction) {
        this.updateForm(inventoryTransaction);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const inventoryTransaction = this.inventoryTransactionFormService.getInventoryTransaction(this.editForm);
    if (inventoryTransaction.id !== null) {
      this.subscribeToSaveResponse(this.inventoryTransactionService.update(inventoryTransaction));
    } else {
      this.subscribeToSaveResponse(this.inventoryTransactionService.create(inventoryTransaction));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IInventoryTransaction>>): void {
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

  protected updateForm(inventoryTransaction: IInventoryTransaction): void {
    this.inventoryTransaction = inventoryTransaction;
    this.inventoryTransactionFormService.resetForm(this.editForm, inventoryTransaction);

    this.productsSharedCollection = this.productService.addProductToCollectionIfMissing<IProduct>(
      this.productsSharedCollection,
      inventoryTransaction.product,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.productService
      .query()
      .pipe(map((res: HttpResponse<IProduct[]>) => res.body ?? []))
      .pipe(
        map((products: IProduct[]) =>
          this.productService.addProductToCollectionIfMissing<IProduct>(products, this.inventoryTransaction?.product),
        ),
      )
      .subscribe((products: IProduct[]) => (this.productsSharedCollection = products));
  }
}

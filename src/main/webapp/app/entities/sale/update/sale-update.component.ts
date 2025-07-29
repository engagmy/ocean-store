import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ISaleOperation } from 'app/entities/sale-operation/sale-operation.model';
import { SaleOperationService } from 'app/entities/sale-operation/service/sale-operation.service';
import { IProduct } from 'app/entities/product/product.model';
import { ProductService } from 'app/entities/product/service/product.service';
import { SaleService } from '../service/sale.service';
import { ISale } from '../sale.model';
import { SaleFormGroup, SaleFormService } from './sale-form.service';

@Component({
  selector: 'jhi-sale-update',
  templateUrl: './sale-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class SaleUpdateComponent implements OnInit {
  isSaving = false;
  sale: ISale | null = null;

  saleOperationsSharedCollection: ISaleOperation[] = [];
  productsSharedCollection: IProduct[] = [];

  protected saleService = inject(SaleService);
  protected saleFormService = inject(SaleFormService);
  protected saleOperationService = inject(SaleOperationService);
  protected productService = inject(ProductService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: SaleFormGroup = this.saleFormService.createSaleFormGroup();

  compareSaleOperation = (o1: ISaleOperation | null, o2: ISaleOperation | null): boolean =>
    this.saleOperationService.compareSaleOperation(o1, o2);

  compareProduct = (o1: IProduct | null, o2: IProduct | null): boolean => this.productService.compareProduct(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ sale }) => {
      this.sale = sale;
      if (sale) {
        this.updateForm(sale);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const sale = this.saleFormService.getSale(this.editForm);
    if (sale.id !== null) {
      this.subscribeToSaveResponse(this.saleService.update(sale));
    } else {
      this.subscribeToSaveResponse(this.saleService.create(sale));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISale>>): void {
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

  protected updateForm(sale: ISale): void {
    this.sale = sale;
    this.saleFormService.resetForm(this.editForm, sale);

    this.saleOperationsSharedCollection = this.saleOperationService.addSaleOperationToCollectionIfMissing<ISaleOperation>(
      this.saleOperationsSharedCollection,
      sale.saleOperation,
    );
    this.productsSharedCollection = this.productService.addProductToCollectionIfMissing<IProduct>(
      this.productsSharedCollection,
      sale.product,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.saleOperationService
      .query()
      .pipe(map((res: HttpResponse<ISaleOperation[]>) => res.body ?? []))
      .pipe(
        map((saleOperations: ISaleOperation[]) =>
          this.saleOperationService.addSaleOperationToCollectionIfMissing<ISaleOperation>(saleOperations, this.sale?.saleOperation),
        ),
      )
      .subscribe((saleOperations: ISaleOperation[]) => (this.saleOperationsSharedCollection = saleOperations));

    this.productService
      .query()
      .pipe(map((res: HttpResponse<IProduct[]>) => res.body ?? []))
      .pipe(map((products: IProduct[]) => this.productService.addProductToCollectionIfMissing<IProduct>(products, this.sale?.product)))
      .subscribe((products: IProduct[]) => (this.productsSharedCollection = products));
  }
}

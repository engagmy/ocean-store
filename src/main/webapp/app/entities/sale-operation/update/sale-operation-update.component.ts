import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IBill } from 'app/entities/bill/bill.model';
import { BillService } from 'app/entities/bill/service/bill.service';
import { ICustomer } from 'app/entities/customer/customer.model';
import { CustomerService } from 'app/entities/customer/service/customer.service';
import { SaleOperationService } from '../service/sale-operation.service';
import { ISaleOperation } from '../sale-operation.model';
import { SaleOperationFormGroup, SaleOperationFormService } from './sale-operation-form.service';

@Component({
  selector: 'jhi-sale-operation-update',
  templateUrl: './sale-operation-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class SaleOperationUpdateComponent implements OnInit {
  isSaving = false;
  saleOperation: ISaleOperation | null = null;

  billsSharedCollection: IBill[] = [];
  customersSharedCollection: ICustomer[] = [];

  protected saleOperationService = inject(SaleOperationService);
  protected saleOperationFormService = inject(SaleOperationFormService);
  protected billService = inject(BillService);
  protected customerService = inject(CustomerService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: SaleOperationFormGroup = this.saleOperationFormService.createSaleOperationFormGroup();

  compareBill = (o1: IBill | null, o2: IBill | null): boolean => this.billService.compareBill(o1, o2);

  compareCustomer = (o1: ICustomer | null, o2: ICustomer | null): boolean => this.customerService.compareCustomer(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ saleOperation }) => {
      this.saleOperation = saleOperation;
      if (saleOperation) {
        this.updateForm(saleOperation);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const saleOperation = this.saleOperationFormService.getSaleOperation(this.editForm);
    if (saleOperation.id !== null) {
      this.subscribeToSaveResponse(this.saleOperationService.update(saleOperation));
    } else {
      this.subscribeToSaveResponse(this.saleOperationService.create(saleOperation));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISaleOperation>>): void {
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

  protected updateForm(saleOperation: ISaleOperation): void {
    this.saleOperation = saleOperation;
    this.saleOperationFormService.resetForm(this.editForm, saleOperation);

    this.billsSharedCollection = this.billService.addBillToCollectionIfMissing<IBill>(this.billsSharedCollection, saleOperation.bill);
    this.customersSharedCollection = this.customerService.addCustomerToCollectionIfMissing<ICustomer>(
      this.customersSharedCollection,
      saleOperation.customer,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.billService
      .query()
      .pipe(map((res: HttpResponse<IBill[]>) => res.body ?? []))
      .pipe(map((bills: IBill[]) => this.billService.addBillToCollectionIfMissing<IBill>(bills, this.saleOperation?.bill)))
      .subscribe((bills: IBill[]) => (this.billsSharedCollection = bills));

    this.customerService
      .query()
      .pipe(map((res: HttpResponse<ICustomer[]>) => res.body ?? []))
      .pipe(
        map((customers: ICustomer[]) =>
          this.customerService.addCustomerToCollectionIfMissing<ICustomer>(customers, this.saleOperation?.customer),
        ),
      )
      .subscribe((customers: ICustomer[]) => (this.customersSharedCollection = customers));
  }
}

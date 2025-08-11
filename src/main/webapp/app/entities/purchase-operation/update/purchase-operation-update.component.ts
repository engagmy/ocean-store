import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IBill } from 'app/entities/bill/bill.model';
import { BillService } from 'app/entities/bill/service/bill.service';
import { IPurchaseOperation } from '../purchase-operation.model';
import { PurchaseOperationService } from '../service/purchase-operation.service';
import { PurchaseOperationFormGroup, PurchaseOperationFormService } from './purchase-operation-form.service';

@Component({
  selector: 'jhi-purchase-operation-update',
  templateUrl: './purchase-operation-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class PurchaseOperationUpdateComponent implements OnInit {
  isSaving = false;
  purchaseOperation: IPurchaseOperation | null = null;

  billsSharedCollection: IBill[] = [];

  protected purchaseOperationService = inject(PurchaseOperationService);
  protected purchaseOperationFormService = inject(PurchaseOperationFormService);
  protected billService = inject(BillService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: PurchaseOperationFormGroup = this.purchaseOperationFormService.createPurchaseOperationFormGroup();

  compareBill = (o1: IBill | null, o2: IBill | null): boolean => this.billService.compareBill(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ purchaseOperation }) => {
      this.purchaseOperation = purchaseOperation;
      if (purchaseOperation) {
        this.updateForm(purchaseOperation);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const purchaseOperation = this.purchaseOperationFormService.getPurchaseOperation(this.editForm);
    if (purchaseOperation.id !== null) {
      this.subscribeToSaveResponse(this.purchaseOperationService.update(purchaseOperation));
    } else {
      this.subscribeToSaveResponse(this.purchaseOperationService.create(purchaseOperation));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPurchaseOperation>>): void {
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

  protected updateForm(purchaseOperation: IPurchaseOperation): void {
    this.purchaseOperation = purchaseOperation;
    this.purchaseOperationFormService.resetForm(this.editForm, purchaseOperation);

    this.billsSharedCollection = this.billService.addBillToCollectionIfMissing<IBill>(this.billsSharedCollection, purchaseOperation.bill);
  }

  protected loadRelationshipsOptions(): void {
    this.billService
      .query()
      .pipe(map((res: HttpResponse<IBill[]>) => res.body ?? []))
      .pipe(map((bills: IBill[]) => this.billService.addBillToCollectionIfMissing<IBill>(bills, this.purchaseOperation?.bill)))
      .subscribe((bills: IBill[]) => (this.billsSharedCollection = bills));
  }
}

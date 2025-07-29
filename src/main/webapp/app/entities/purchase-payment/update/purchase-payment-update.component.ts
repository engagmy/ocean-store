import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IPurchase } from 'app/entities/purchase/purchase.model';
import { PurchaseService } from 'app/entities/purchase/service/purchase.service';
import { IPurchasePayment } from '../purchase-payment.model';
import { PurchasePaymentService } from '../service/purchase-payment.service';
import { PurchasePaymentFormGroup, PurchasePaymentFormService } from './purchase-payment-form.service';

@Component({
  selector: 'jhi-purchase-payment-update',
  templateUrl: './purchase-payment-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class PurchasePaymentUpdateComponent implements OnInit {
  isSaving = false;
  purchasePayment: IPurchasePayment | null = null;

  purchasesSharedCollection: IPurchase[] = [];

  protected purchasePaymentService = inject(PurchasePaymentService);
  protected purchasePaymentFormService = inject(PurchasePaymentFormService);
  protected purchaseService = inject(PurchaseService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: PurchasePaymentFormGroup = this.purchasePaymentFormService.createPurchasePaymentFormGroup();

  comparePurchase = (o1: IPurchase | null, o2: IPurchase | null): boolean => this.purchaseService.comparePurchase(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ purchasePayment }) => {
      this.purchasePayment = purchasePayment;
      if (purchasePayment) {
        this.updateForm(purchasePayment);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const purchasePayment = this.purchasePaymentFormService.getPurchasePayment(this.editForm);
    if (purchasePayment.id !== null) {
      this.subscribeToSaveResponse(this.purchasePaymentService.update(purchasePayment));
    } else {
      this.subscribeToSaveResponse(this.purchasePaymentService.create(purchasePayment));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPurchasePayment>>): void {
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

  protected updateForm(purchasePayment: IPurchasePayment): void {
    this.purchasePayment = purchasePayment;
    this.purchasePaymentFormService.resetForm(this.editForm, purchasePayment);

    this.purchasesSharedCollection = this.purchaseService.addPurchaseToCollectionIfMissing<IPurchase>(
      this.purchasesSharedCollection,
      purchasePayment.purchase,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.purchaseService
      .query()
      .pipe(map((res: HttpResponse<IPurchase[]>) => res.body ?? []))
      .pipe(
        map((purchases: IPurchase[]) =>
          this.purchaseService.addPurchaseToCollectionIfMissing<IPurchase>(purchases, this.purchasePayment?.purchase),
        ),
      )
      .subscribe((purchases: IPurchase[]) => (this.purchasesSharedCollection = purchases));
  }
}

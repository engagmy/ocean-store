import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ISale } from 'app/entities/sale/sale.model';
import { SaleService } from 'app/entities/sale/service/sale.service';
import { ISalePayment } from '../sale-payment.model';
import { SalePaymentService } from '../service/sale-payment.service';
import { SalePaymentFormGroup, SalePaymentFormService } from './sale-payment-form.service';

@Component({
  selector: 'jhi-sale-payment-update',
  templateUrl: './sale-payment-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class SalePaymentUpdateComponent implements OnInit {
  isSaving = false;
  salePayment: ISalePayment | null = null;

  salesSharedCollection: ISale[] = [];

  protected salePaymentService = inject(SalePaymentService);
  protected salePaymentFormService = inject(SalePaymentFormService);
  protected saleService = inject(SaleService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: SalePaymentFormGroup = this.salePaymentFormService.createSalePaymentFormGroup();

  compareSale = (o1: ISale | null, o2: ISale | null): boolean => this.saleService.compareSale(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ salePayment }) => {
      this.salePayment = salePayment;
      if (salePayment) {
        this.updateForm(salePayment);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const salePayment = this.salePaymentFormService.getSalePayment(this.editForm);
    if (salePayment.id !== null) {
      this.subscribeToSaveResponse(this.salePaymentService.update(salePayment));
    } else {
      this.subscribeToSaveResponse(this.salePaymentService.create(salePayment));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISalePayment>>): void {
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

  protected updateForm(salePayment: ISalePayment): void {
    this.salePayment = salePayment;
    this.salePaymentFormService.resetForm(this.editForm, salePayment);

    this.salesSharedCollection = this.saleService.addSaleToCollectionIfMissing<ISale>(this.salesSharedCollection, salePayment.sale);
  }

  protected loadRelationshipsOptions(): void {
    this.saleService
      .query()
      .pipe(map((res: HttpResponse<ISale[]>) => res.body ?? []))
      .pipe(map((sales: ISale[]) => this.saleService.addSaleToCollectionIfMissing<ISale>(sales, this.salePayment?.sale)))
      .subscribe((sales: ISale[]) => (this.salesSharedCollection = sales));
  }
}

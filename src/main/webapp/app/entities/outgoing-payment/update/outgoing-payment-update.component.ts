import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IOutgoingPayment } from '../outgoing-payment.model';
import { OutgoingPaymentService } from '../service/outgoing-payment.service';
import { OutgoingPaymentFormGroup, OutgoingPaymentFormService } from './outgoing-payment-form.service';

@Component({
  selector: 'jhi-outgoing-payment-update',
  templateUrl: './outgoing-payment-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class OutgoingPaymentUpdateComponent implements OnInit {
  isSaving = false;
  outgoingPayment: IOutgoingPayment | null = null;

  protected outgoingPaymentService = inject(OutgoingPaymentService);
  protected outgoingPaymentFormService = inject(OutgoingPaymentFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: OutgoingPaymentFormGroup = this.outgoingPaymentFormService.createOutgoingPaymentFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ outgoingPayment }) => {
      this.outgoingPayment = outgoingPayment;
      if (outgoingPayment) {
        this.updateForm(outgoingPayment);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const outgoingPayment = this.outgoingPaymentFormService.getOutgoingPayment(this.editForm);
    if (outgoingPayment.id !== null) {
      this.subscribeToSaveResponse(this.outgoingPaymentService.update(outgoingPayment));
    } else {
      this.subscribeToSaveResponse(this.outgoingPaymentService.create(outgoingPayment));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IOutgoingPayment>>): void {
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

  protected updateForm(outgoingPayment: IOutgoingPayment): void {
    this.outgoingPayment = outgoingPayment;
    this.outgoingPaymentFormService.resetForm(this.editForm, outgoingPayment);
  }
}

import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IBill } from '../bill.model';
import { BillService } from '../service/bill.service';
import { BillFormGroup, BillFormService } from './bill-form.service';

@Component({
  selector: 'jhi-bill-update',
  templateUrl: './bill-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class BillUpdateComponent implements OnInit {
  isSaving = false;
  bill: IBill | null = null;

  protected billService = inject(BillService);
  protected billFormService = inject(BillFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: BillFormGroup = this.billFormService.createBillFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ bill }) => {
      this.bill = bill;
      if (bill) {
        this.updateForm(bill);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const bill = this.billFormService.getBill(this.editForm);
    if (bill.id !== null) {
      this.subscribeToSaveResponse(this.billService.update(bill));
    } else {
      this.subscribeToSaveResponse(this.billService.create(bill));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IBill>>): void {
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

  protected updateForm(bill: IBill): void {
    this.bill = bill;
    this.billFormService.resetForm(this.editForm, bill);
  }
}

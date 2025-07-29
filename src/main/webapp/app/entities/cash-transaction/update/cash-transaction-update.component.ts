import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { CashTransactionType } from 'app/entities/enumerations/cash-transaction-type.model';
import { ICashTransaction } from '../cash-transaction.model';
import { CashTransactionService } from '../service/cash-transaction.service';
import { CashTransactionFormGroup, CashTransactionFormService } from './cash-transaction-form.service';

@Component({
  selector: 'jhi-cash-transaction-update',
  templateUrl: './cash-transaction-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class CashTransactionUpdateComponent implements OnInit {
  isSaving = false;
  cashTransaction: ICashTransaction | null = null;
  cashTransactionTypeValues = Object.keys(CashTransactionType);

  protected cashTransactionService = inject(CashTransactionService);
  protected cashTransactionFormService = inject(CashTransactionFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: CashTransactionFormGroup = this.cashTransactionFormService.createCashTransactionFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ cashTransaction }) => {
      this.cashTransaction = cashTransaction;
      if (cashTransaction) {
        this.updateForm(cashTransaction);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const cashTransaction = this.cashTransactionFormService.getCashTransaction(this.editForm);
    if (cashTransaction.id !== null) {
      this.subscribeToSaveResponse(this.cashTransactionService.update(cashTransaction));
    } else {
      this.subscribeToSaveResponse(this.cashTransactionService.create(cashTransaction));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICashTransaction>>): void {
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

  protected updateForm(cashTransaction: ICashTransaction): void {
    this.cashTransaction = cashTransaction;
    this.cashTransactionFormService.resetForm(this.editForm, cashTransaction);
  }
}

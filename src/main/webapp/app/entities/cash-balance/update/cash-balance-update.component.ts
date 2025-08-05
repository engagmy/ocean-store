import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ICashBalance } from '../cash-balance.model';
import { CashBalanceService } from '../service/cash-balance.service';
import { CashBalanceFormGroup, CashBalanceFormService } from './cash-balance-form.service';

@Component({
  selector: 'jhi-cash-balance-update',
  templateUrl: './cash-balance-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class CashBalanceUpdateComponent implements OnInit {
  isSaving = false;
  cashBalance: ICashBalance | null = null;

  protected cashBalanceService = inject(CashBalanceService);
  protected cashBalanceFormService = inject(CashBalanceFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: CashBalanceFormGroup = this.cashBalanceFormService.createCashBalanceFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ cashBalance }) => {
      this.cashBalance = cashBalance;
      if (cashBalance) {
        this.updateForm(cashBalance);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const cashBalance = this.cashBalanceFormService.getCashBalance(this.editForm);
    if (cashBalance.id !== null) {
      this.subscribeToSaveResponse(this.cashBalanceService.update(cashBalance));
    } else {
      this.subscribeToSaveResponse(this.cashBalanceService.create(cashBalance));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICashBalance>>): void {
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

  protected updateForm(cashBalance: ICashBalance): void {
    this.cashBalance = cashBalance;
    this.cashBalanceFormService.resetForm(this.editForm, cashBalance);
  }
}

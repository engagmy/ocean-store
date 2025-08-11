import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IDailyCashReconciliation } from 'app/entities/daily-cash-reconciliation/daily-cash-reconciliation.model';
import { DailyCashReconciliationService } from 'app/entities/daily-cash-reconciliation/service/daily-cash-reconciliation.service';
import { CashTransactionType } from 'app/entities/enumerations/cash-transaction-type.model';
import { DailyCashDetailService } from '../service/daily-cash-detail.service';
import { IDailyCashDetail } from '../daily-cash-detail.model';
import { DailyCashDetailFormGroup, DailyCashDetailFormService } from './daily-cash-detail-form.service';

@Component({
  selector: 'jhi-daily-cash-detail-update',
  templateUrl: './daily-cash-detail-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class DailyCashDetailUpdateComponent implements OnInit {
  isSaving = false;
  dailyCashDetail: IDailyCashDetail | null = null;
  cashTransactionTypeValues = Object.keys(CashTransactionType);

  dailyCashReconciliationsSharedCollection: IDailyCashReconciliation[] = [];

  protected dailyCashDetailService = inject(DailyCashDetailService);
  protected dailyCashDetailFormService = inject(DailyCashDetailFormService);
  protected dailyCashReconciliationService = inject(DailyCashReconciliationService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: DailyCashDetailFormGroup = this.dailyCashDetailFormService.createDailyCashDetailFormGroup();

  compareDailyCashReconciliation = (o1: IDailyCashReconciliation | null, o2: IDailyCashReconciliation | null): boolean =>
    this.dailyCashReconciliationService.compareDailyCashReconciliation(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ dailyCashDetail }) => {
      this.dailyCashDetail = dailyCashDetail;
      if (dailyCashDetail) {
        this.updateForm(dailyCashDetail);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const dailyCashDetail = this.dailyCashDetailFormService.getDailyCashDetail(this.editForm);
    if (dailyCashDetail.id !== null) {
      this.subscribeToSaveResponse(this.dailyCashDetailService.update(dailyCashDetail));
    } else {
      this.subscribeToSaveResponse(this.dailyCashDetailService.create(dailyCashDetail));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IDailyCashDetail>>): void {
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

  protected updateForm(dailyCashDetail: IDailyCashDetail): void {
    this.dailyCashDetail = dailyCashDetail;
    this.dailyCashDetailFormService.resetForm(this.editForm, dailyCashDetail);

    this.dailyCashReconciliationsSharedCollection =
      this.dailyCashReconciliationService.addDailyCashReconciliationToCollectionIfMissing<IDailyCashReconciliation>(
        this.dailyCashReconciliationsSharedCollection,
        dailyCashDetail.dailyCashReconciliation,
      );
  }

  protected loadRelationshipsOptions(): void {
    this.dailyCashReconciliationService
      .query()
      .pipe(map((res: HttpResponse<IDailyCashReconciliation[]>) => res.body ?? []))
      .pipe(
        map((dailyCashReconciliations: IDailyCashReconciliation[]) =>
          this.dailyCashReconciliationService.addDailyCashReconciliationToCollectionIfMissing<IDailyCashReconciliation>(
            dailyCashReconciliations,
            this.dailyCashDetail?.dailyCashReconciliation,
          ),
        ),
      )
      .subscribe(
        (dailyCashReconciliations: IDailyCashReconciliation[]) =>
          (this.dailyCashReconciliationsSharedCollection = dailyCashReconciliations),
      );
  }
}

import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IDailyCashReconciliation } from '../daily-cash-reconciliation.model';
import { DailyCashReconciliationService } from '../service/daily-cash-reconciliation.service';
import { DailyCashReconciliationFormGroup, DailyCashReconciliationFormService } from './daily-cash-reconciliation-form.service';

@Component({
  selector: 'jhi-daily-cash-reconciliation-update',
  templateUrl: './daily-cash-reconciliation-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class DailyCashReconciliationUpdateComponent implements OnInit {
  isSaving = false;
  dailyCashReconciliation: IDailyCashReconciliation | null = null;

  protected dailyCashReconciliationService = inject(DailyCashReconciliationService);
  protected dailyCashReconciliationFormService = inject(DailyCashReconciliationFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: DailyCashReconciliationFormGroup = this.dailyCashReconciliationFormService.createDailyCashReconciliationFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ dailyCashReconciliation }) => {
      this.dailyCashReconciliation = dailyCashReconciliation;
      if (dailyCashReconciliation) {
        this.updateForm(dailyCashReconciliation);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const dailyCashReconciliation = this.dailyCashReconciliationFormService.getDailyCashReconciliation(this.editForm);
    if (dailyCashReconciliation.id !== null) {
      this.subscribeToSaveResponse(this.dailyCashReconciliationService.update(dailyCashReconciliation));
    } else {
      this.subscribeToSaveResponse(this.dailyCashReconciliationService.create(dailyCashReconciliation));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IDailyCashReconciliation>>): void {
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

  protected updateForm(dailyCashReconciliation: IDailyCashReconciliation): void {
    this.dailyCashReconciliation = dailyCashReconciliation;
    this.dailyCashReconciliationFormService.resetForm(this.editForm, dailyCashReconciliation);
  }
}

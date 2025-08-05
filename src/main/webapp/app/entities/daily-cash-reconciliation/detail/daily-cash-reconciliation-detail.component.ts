import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { IDailyCashReconciliation } from '../daily-cash-reconciliation.model';

@Component({
  selector: 'jhi-daily-cash-reconciliation-detail',
  templateUrl: './daily-cash-reconciliation-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class DailyCashReconciliationDetailComponent {
  dailyCashReconciliation = input<IDailyCashReconciliation | null>(null);

  previousState(): void {
    window.history.back();
  }
}

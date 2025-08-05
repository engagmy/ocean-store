import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { ICashTransaction } from '../cash-transaction.model';

@Component({
  selector: 'jhi-cash-transaction-detail',
  templateUrl: './cash-transaction-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe],
})
export class CashTransactionDetailComponent {
  cashTransaction = input<ICashTransaction | null>(null);

  previousState(): void {
    window.history.back();
  }
}

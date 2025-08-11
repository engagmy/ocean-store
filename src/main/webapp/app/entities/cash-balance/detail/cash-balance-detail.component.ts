import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { ICashBalance } from '../cash-balance.model';

@Component({
  selector: 'jhi-cash-balance-detail',
  templateUrl: './cash-balance-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe],
})
export class CashBalanceDetailComponent {
  cashBalance = input<ICashBalance | null>(null);

  previousState(): void {
    window.history.back();
  }
}

import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { ISalePayment } from '../sale-payment.model';

@Component({
  selector: 'jhi-sale-payment-detail',
  templateUrl: './sale-payment-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe],
})
export class SalePaymentDetailComponent {
  salePayment = input<ISalePayment | null>(null);

  previousState(): void {
    window.history.back();
  }
}

import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { IPurchasePayment } from '../purchase-payment.model';

@Component({
  selector: 'jhi-purchase-payment-detail',
  templateUrl: './purchase-payment-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe],
})
export class PurchasePaymentDetailComponent {
  purchasePayment = input<IPurchasePayment | null>(null);

  previousState(): void {
    window.history.back();
  }
}

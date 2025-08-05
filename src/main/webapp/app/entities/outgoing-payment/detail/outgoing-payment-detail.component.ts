import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { IOutgoingPayment } from '../outgoing-payment.model';

@Component({
  selector: 'jhi-outgoing-payment-detail',
  templateUrl: './outgoing-payment-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe],
})
export class OutgoingPaymentDetailComponent {
  outgoingPayment = input<IOutgoingPayment | null>(null);

  previousState(): void {
    window.history.back();
  }
}

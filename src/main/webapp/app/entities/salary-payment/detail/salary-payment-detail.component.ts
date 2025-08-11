import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { ISalaryPayment } from '../salary-payment.model';

@Component({
  selector: 'jhi-salary-payment-detail',
  templateUrl: './salary-payment-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe],
})
export class SalaryPaymentDetailComponent {
  salaryPayment = input<ISalaryPayment | null>(null);

  previousState(): void {
    window.history.back();
  }
}

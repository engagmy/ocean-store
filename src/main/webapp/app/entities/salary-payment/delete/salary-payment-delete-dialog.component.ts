import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { ISalaryPayment } from '../salary-payment.model';
import { SalaryPaymentService } from '../service/salary-payment.service';

@Component({
  templateUrl: './salary-payment-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class SalaryPaymentDeleteDialogComponent {
  salaryPayment?: ISalaryPayment;

  protected salaryPaymentService = inject(SalaryPaymentService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.salaryPaymentService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}

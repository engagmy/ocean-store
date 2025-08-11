import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { ISalePayment } from '../sale-payment.model';
import { SalePaymentService } from '../service/sale-payment.service';

@Component({
  templateUrl: './sale-payment-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class SalePaymentDeleteDialogComponent {
  salePayment?: ISalePayment;

  protected salePaymentService = inject(SalePaymentService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.salePaymentService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}

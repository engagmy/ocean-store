import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IPurchasePayment } from '../purchase-payment.model';
import { PurchasePaymentService } from '../service/purchase-payment.service';

@Component({
  templateUrl: './purchase-payment-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class PurchasePaymentDeleteDialogComponent {
  purchasePayment?: IPurchasePayment;

  protected purchasePaymentService = inject(PurchasePaymentService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.purchasePaymentService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}

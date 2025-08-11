import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IOutgoingPayment } from '../outgoing-payment.model';
import { OutgoingPaymentService } from '../service/outgoing-payment.service';

@Component({
  templateUrl: './outgoing-payment-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class OutgoingPaymentDeleteDialogComponent {
  outgoingPayment?: IOutgoingPayment;

  protected outgoingPaymentService = inject(OutgoingPaymentService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.outgoingPaymentService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}

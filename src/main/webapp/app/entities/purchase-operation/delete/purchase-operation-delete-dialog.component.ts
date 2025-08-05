import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IPurchaseOperation } from '../purchase-operation.model';
import { PurchaseOperationService } from '../service/purchase-operation.service';

@Component({
  templateUrl: './purchase-operation-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class PurchaseOperationDeleteDialogComponent {
  purchaseOperation?: IPurchaseOperation;

  protected purchaseOperationService = inject(PurchaseOperationService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.purchaseOperationService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}

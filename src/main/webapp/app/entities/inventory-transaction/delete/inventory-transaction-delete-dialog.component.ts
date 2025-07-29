import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IInventoryTransaction } from '../inventory-transaction.model';
import { InventoryTransactionService } from '../service/inventory-transaction.service';

@Component({
  templateUrl: './inventory-transaction-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class InventoryTransactionDeleteDialogComponent {
  inventoryTransaction?: IInventoryTransaction;

  protected inventoryTransactionService = inject(InventoryTransactionService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.inventoryTransactionService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}

import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { ISaleOperation } from '../sale-operation.model';
import { SaleOperationService } from '../service/sale-operation.service';

@Component({
  templateUrl: './sale-operation-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class SaleOperationDeleteDialogComponent {
  saleOperation?: ISaleOperation;

  protected saleOperationService = inject(SaleOperationService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.saleOperationService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}

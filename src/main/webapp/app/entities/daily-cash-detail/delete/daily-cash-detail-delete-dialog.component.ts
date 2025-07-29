import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IDailyCashDetail } from '../daily-cash-detail.model';
import { DailyCashDetailService } from '../service/daily-cash-detail.service';

@Component({
  templateUrl: './daily-cash-detail-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class DailyCashDetailDeleteDialogComponent {
  dailyCashDetail?: IDailyCashDetail;

  protected dailyCashDetailService = inject(DailyCashDetailService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.dailyCashDetailService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}

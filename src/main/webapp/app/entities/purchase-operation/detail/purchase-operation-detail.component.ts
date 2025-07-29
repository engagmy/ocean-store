import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { IPurchaseOperation } from '../purchase-operation.model';

@Component({
  selector: 'jhi-purchase-operation-detail',
  templateUrl: './purchase-operation-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe],
})
export class PurchaseOperationDetailComponent {
  purchaseOperation = input<IPurchaseOperation | null>(null);

  previousState(): void {
    window.history.back();
  }
}

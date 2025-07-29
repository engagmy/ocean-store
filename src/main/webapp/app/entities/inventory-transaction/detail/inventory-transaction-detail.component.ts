import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { IInventoryTransaction } from '../inventory-transaction.model';

@Component({
  selector: 'jhi-inventory-transaction-detail',
  templateUrl: './inventory-transaction-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe],
})
export class InventoryTransactionDetailComponent {
  inventoryTransaction = input<IInventoryTransaction | null>(null);

  previousState(): void {
    window.history.back();
  }
}

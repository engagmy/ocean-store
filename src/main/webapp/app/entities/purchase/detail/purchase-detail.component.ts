import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { IPurchase } from '../purchase.model';

@Component({
  selector: 'jhi-purchase-detail',
  templateUrl: './purchase-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe],
})
export class PurchaseDetailComponent {
  purchase = input<IPurchase | null>(null);

  previousState(): void {
    window.history.back();
  }
}

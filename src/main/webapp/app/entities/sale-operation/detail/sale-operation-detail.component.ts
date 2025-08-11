import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { ISaleOperation } from '../sale-operation.model';

@Component({
  selector: 'jhi-sale-operation-detail',
  templateUrl: './sale-operation-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe],
})
export class SaleOperationDetailComponent {
  saleOperation = input<ISaleOperation | null>(null);

  previousState(): void {
    window.history.back();
  }
}

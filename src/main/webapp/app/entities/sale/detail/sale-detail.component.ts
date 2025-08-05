import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { ISale } from '../sale.model';

@Component({
  selector: 'jhi-sale-detail',
  templateUrl: './sale-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe],
})
export class SaleDetailComponent {
  sale = input<ISale | null>(null);

  previousState(): void {
    window.history.back();
  }
}

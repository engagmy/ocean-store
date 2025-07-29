import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { IBill } from '../bill.model';

@Component({
  selector: 'jhi-bill-detail',
  templateUrl: './bill-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe],
})
export class BillDetailComponent {
  bill = input<IBill | null>(null);

  previousState(): void {
    window.history.back();
  }
}

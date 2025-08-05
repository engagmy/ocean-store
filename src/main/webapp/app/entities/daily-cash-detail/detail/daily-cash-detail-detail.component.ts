import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { IDailyCashDetail } from '../daily-cash-detail.model';

@Component({
  selector: 'jhi-daily-cash-detail-detail',
  templateUrl: './daily-cash-detail-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe],
})
export class DailyCashDetailDetailComponent {
  dailyCashDetail = input<IDailyCashDetail | null>(null);

  previousState(): void {
    window.history.back();
  }
}

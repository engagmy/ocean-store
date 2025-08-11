import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { IProductCategory } from '../product-category.model';

@Component({
  selector: 'jhi-product-category-detail',
  templateUrl: './product-category-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe],
})
export class ProductCategoryDetailComponent {
  productCategory = input<IProductCategory | null>(null);

  previousState(): void {
    window.history.back();
  }
}

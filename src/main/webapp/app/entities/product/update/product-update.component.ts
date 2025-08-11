import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IBrand } from 'app/entities/brand/brand.model';
import { BrandService } from 'app/entities/brand/service/brand.service';
import { IProductCategory } from 'app/entities/product-category/product-category.model';
import { ProductCategoryService } from 'app/entities/product-category/service/product-category.service';
import { ProductService } from '../service/product.service';
import { IProduct } from '../product.model';
import { ProductFormGroup, ProductFormService } from './product-form.service';

@Component({
  selector: 'jhi-product-update',
  templateUrl: './product-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class ProductUpdateComponent implements OnInit {
  isSaving = false;
  product: IProduct | null = null;

  brandsSharedCollection: IBrand[] = [];
  productCategoriesSharedCollection: IProductCategory[] = [];

  protected productService = inject(ProductService);
  protected productFormService = inject(ProductFormService);
  protected brandService = inject(BrandService);
  protected productCategoryService = inject(ProductCategoryService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ProductFormGroup = this.productFormService.createProductFormGroup();

  compareBrand = (o1: IBrand | null, o2: IBrand | null): boolean => this.brandService.compareBrand(o1, o2);

  compareProductCategory = (o1: IProductCategory | null, o2: IProductCategory | null): boolean =>
    this.productCategoryService.compareProductCategory(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ product }) => {
      this.product = product;
      if (product) {
        this.updateForm(product);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const product = this.productFormService.getProduct(this.editForm);
    if (product.id !== null) {
      this.subscribeToSaveResponse(this.productService.update(product));
    } else {
      this.subscribeToSaveResponse(this.productService.create(product));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IProduct>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(product: IProduct): void {
    this.product = product;
    this.productFormService.resetForm(this.editForm, product);

    this.brandsSharedCollection = this.brandService.addBrandToCollectionIfMissing<IBrand>(this.brandsSharedCollection, product.brand);
    this.productCategoriesSharedCollection = this.productCategoryService.addProductCategoryToCollectionIfMissing<IProductCategory>(
      this.productCategoriesSharedCollection,
      product.productCategory,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.brandService
      .query()
      .pipe(map((res: HttpResponse<IBrand[]>) => res.body ?? []))
      .pipe(map((brands: IBrand[]) => this.brandService.addBrandToCollectionIfMissing<IBrand>(brands, this.product?.brand)))
      .subscribe((brands: IBrand[]) => (this.brandsSharedCollection = brands));

    this.productCategoryService
      .query()
      .pipe(map((res: HttpResponse<IProductCategory[]>) => res.body ?? []))
      .pipe(
        map((productCategories: IProductCategory[]) =>
          this.productCategoryService.addProductCategoryToCollectionIfMissing<IProductCategory>(
            productCategories,
            this.product?.productCategory,
          ),
        ),
      )
      .subscribe((productCategories: IProductCategory[]) => (this.productCategoriesSharedCollection = productCategories));
  }
}

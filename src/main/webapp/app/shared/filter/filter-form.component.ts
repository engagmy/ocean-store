import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { FilterField, FilterOptions, IFilterOptions } from './filter.model';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import TranslateDirective from '../language/translate.directive';

@Component({
  selector: 'jhi-filter-form',
  templateUrl: './filter-form.component.html',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, TranslateDirective],
})
export class FilterFormComponent implements OnInit {
  @Input() filters: IFilterOptions = new FilterOptions([]);
  @Input() fields: FilterField[] = [];

  @Output() filterSubmit = new EventEmitter<void>();
  @Output() filterReset = new EventEmitter<void>();

  form: FormGroup = this.fb.group({});

  constructor(private fb: FormBuilder) {}

  ngOnInit(): void {
    for (const field of this.fields) {
      const existingValue = this.filters.getFilterOptionByName(field.key)?.values[0] || '';
      this.form.addControl(field.key, this.fb.control(existingValue));
    }
  }

  submit(): void {
    this.filters.clear(); // Clear previous values
    for (const field of this.fields) {
      const value = this.form.get(field.key)?.value;
      if (value !== null && value !== undefined && value !== '') {
        const suffix = field.type === 'text' ? 'contains' : 'equals';
        const filterKey = `${field.key}.${suffix}`;
        this.filters.addFilter(filterKey, value);
      }
    }
    this.filterSubmit.emit();
  }

  reset(): void {
    this.form.reset();
    this.filters.clear();
    this.filterReset.emit();
  }
}

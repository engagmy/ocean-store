import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IEmployee } from 'app/entities/employee/employee.model';
import { EmployeeService } from 'app/entities/employee/service/employee.service';
import { ISalaryPayment } from '../salary-payment.model';
import { SalaryPaymentService } from '../service/salary-payment.service';
import { SalaryPaymentFormGroup, SalaryPaymentFormService } from './salary-payment-form.service';

@Component({
  selector: 'jhi-salary-payment-update',
  templateUrl: './salary-payment-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class SalaryPaymentUpdateComponent implements OnInit {
  isSaving = false;
  salaryPayment: ISalaryPayment | null = null;

  employeesSharedCollection: IEmployee[] = [];

  protected salaryPaymentService = inject(SalaryPaymentService);
  protected salaryPaymentFormService = inject(SalaryPaymentFormService);
  protected employeeService = inject(EmployeeService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: SalaryPaymentFormGroup = this.salaryPaymentFormService.createSalaryPaymentFormGroup();

  compareEmployee = (o1: IEmployee | null, o2: IEmployee | null): boolean => this.employeeService.compareEmployee(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ salaryPayment }) => {
      this.salaryPayment = salaryPayment;
      if (salaryPayment) {
        this.updateForm(salaryPayment);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const salaryPayment = this.salaryPaymentFormService.getSalaryPayment(this.editForm);
    if (salaryPayment.id !== null) {
      this.subscribeToSaveResponse(this.salaryPaymentService.update(salaryPayment));
    } else {
      this.subscribeToSaveResponse(this.salaryPaymentService.create(salaryPayment));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISalaryPayment>>): void {
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

  protected updateForm(salaryPayment: ISalaryPayment): void {
    this.salaryPayment = salaryPayment;
    this.salaryPaymentFormService.resetForm(this.editForm, salaryPayment);

    this.employeesSharedCollection = this.employeeService.addEmployeeToCollectionIfMissing<IEmployee>(
      this.employeesSharedCollection,
      salaryPayment.employee,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.employeeService
      .query()
      .pipe(map((res: HttpResponse<IEmployee[]>) => res.body ?? []))
      .pipe(
        map((employees: IEmployee[]) =>
          this.employeeService.addEmployeeToCollectionIfMissing<IEmployee>(employees, this.salaryPayment?.employee),
        ),
      )
      .subscribe((employees: IEmployee[]) => (this.employeesSharedCollection = employees));
  }
}

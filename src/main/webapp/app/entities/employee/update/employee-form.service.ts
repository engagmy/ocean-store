import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IEmployee, NewEmployee } from '../employee.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IEmployee for edit and NewEmployeeFormGroupInput for create.
 */
type EmployeeFormGroupInput = IEmployee | PartialWithRequiredKeyOf<NewEmployee>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IEmployee | NewEmployee> = Omit<T, 'joinDate' | 'createdDate' | 'lastModifiedDate'> & {
  joinDate?: string | null;
  createdDate?: string | null;
  lastModifiedDate?: string | null;
};

type EmployeeFormRawValue = FormValueOf<IEmployee>;

type NewEmployeeFormRawValue = FormValueOf<NewEmployee>;

type EmployeeFormDefaults = Pick<NewEmployee, 'id' | 'joinDate' | 'active' | 'createdDate' | 'lastModifiedDate'>;

type EmployeeFormGroupContent = {
  id: FormControl<EmployeeFormRawValue['id'] | NewEmployee['id']>;
  name: FormControl<EmployeeFormRawValue['name']>;
  jobTitle: FormControl<EmployeeFormRawValue['jobTitle']>;
  salary: FormControl<EmployeeFormRawValue['salary']>;
  joinDate: FormControl<EmployeeFormRawValue['joinDate']>;
  active: FormControl<EmployeeFormRawValue['active']>;
  createdBy: FormControl<EmployeeFormRawValue['createdBy']>;
  createdDate: FormControl<EmployeeFormRawValue['createdDate']>;
  lastModifiedBy: FormControl<EmployeeFormRawValue['lastModifiedBy']>;
  lastModifiedDate: FormControl<EmployeeFormRawValue['lastModifiedDate']>;
};

export type EmployeeFormGroup = FormGroup<EmployeeFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class EmployeeFormService {
  createEmployeeFormGroup(employee: EmployeeFormGroupInput = { id: null }): EmployeeFormGroup {
    const employeeRawValue = this.convertEmployeeToEmployeeRawValue({
      ...this.getFormDefaults(),
      ...employee,
    });
    return new FormGroup<EmployeeFormGroupContent>({
      id: new FormControl(
        { value: employeeRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(employeeRawValue.name, {
        validators: [Validators.required],
      }),
      jobTitle: new FormControl(employeeRawValue.jobTitle),
      salary: new FormControl(employeeRawValue.salary, {
        validators: [Validators.required],
      }),
      joinDate: new FormControl(employeeRawValue.joinDate),
      active: new FormControl(employeeRawValue.active),
      createdBy: new FormControl(employeeRawValue.createdBy),
      createdDate: new FormControl(employeeRawValue.createdDate),
      lastModifiedBy: new FormControl(employeeRawValue.lastModifiedBy),
      lastModifiedDate: new FormControl(employeeRawValue.lastModifiedDate),
    });
  }

  getEmployee(form: EmployeeFormGroup): IEmployee | NewEmployee {
    return this.convertEmployeeRawValueToEmployee(form.getRawValue() as EmployeeFormRawValue | NewEmployeeFormRawValue);
  }

  resetForm(form: EmployeeFormGroup, employee: EmployeeFormGroupInput): void {
    const employeeRawValue = this.convertEmployeeToEmployeeRawValue({ ...this.getFormDefaults(), ...employee });
    form.reset(
      {
        ...employeeRawValue,
        id: { value: employeeRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): EmployeeFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      joinDate: currentTime,
      active: false,
      createdDate: currentTime,
      lastModifiedDate: currentTime,
    };
  }

  private convertEmployeeRawValueToEmployee(rawEmployee: EmployeeFormRawValue | NewEmployeeFormRawValue): IEmployee | NewEmployee {
    return {
      ...rawEmployee,
      joinDate: dayjs(rawEmployee.joinDate, DATE_TIME_FORMAT),
      createdDate: dayjs(rawEmployee.createdDate, DATE_TIME_FORMAT),
      lastModifiedDate: dayjs(rawEmployee.lastModifiedDate, DATE_TIME_FORMAT),
    };
  }

  private convertEmployeeToEmployeeRawValue(
    employee: IEmployee | (Partial<NewEmployee> & EmployeeFormDefaults),
  ): EmployeeFormRawValue | PartialWithRequiredKeyOf<NewEmployeeFormRawValue> {
    return {
      ...employee,
      joinDate: employee.joinDate ? employee.joinDate.format(DATE_TIME_FORMAT) : undefined,
      createdDate: employee.createdDate ? employee.createdDate.format(DATE_TIME_FORMAT) : undefined,
      lastModifiedDate: employee.lastModifiedDate ? employee.lastModifiedDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}

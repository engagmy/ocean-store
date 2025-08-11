import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IEmployee } from 'app/entities/employee/employee.model';
import { EmployeeService } from 'app/entities/employee/service/employee.service';
import { SalaryPaymentService } from '../service/salary-payment.service';
import { ISalaryPayment } from '../salary-payment.model';
import { SalaryPaymentFormService } from './salary-payment-form.service';

import { SalaryPaymentUpdateComponent } from './salary-payment-update.component';

describe('SalaryPayment Management Update Component', () => {
  let comp: SalaryPaymentUpdateComponent;
  let fixture: ComponentFixture<SalaryPaymentUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let salaryPaymentFormService: SalaryPaymentFormService;
  let salaryPaymentService: SalaryPaymentService;
  let employeeService: EmployeeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [SalaryPaymentUpdateComponent],
      providers: [
        provideHttpClient(),
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(SalaryPaymentUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(SalaryPaymentUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    salaryPaymentFormService = TestBed.inject(SalaryPaymentFormService);
    salaryPaymentService = TestBed.inject(SalaryPaymentService);
    employeeService = TestBed.inject(EmployeeService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Employee query and add missing value', () => {
      const salaryPayment: ISalaryPayment = { id: 12182 };
      const employee: IEmployee = { id: 1749 };
      salaryPayment.employee = employee;

      const employeeCollection: IEmployee[] = [{ id: 1749 }];
      jest.spyOn(employeeService, 'query').mockReturnValue(of(new HttpResponse({ body: employeeCollection })));
      const additionalEmployees = [employee];
      const expectedCollection: IEmployee[] = [...additionalEmployees, ...employeeCollection];
      jest.spyOn(employeeService, 'addEmployeeToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ salaryPayment });
      comp.ngOnInit();

      expect(employeeService.query).toHaveBeenCalled();
      expect(employeeService.addEmployeeToCollectionIfMissing).toHaveBeenCalledWith(
        employeeCollection,
        ...additionalEmployees.map(expect.objectContaining),
      );
      expect(comp.employeesSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const salaryPayment: ISalaryPayment = { id: 12182 };
      const employee: IEmployee = { id: 1749 };
      salaryPayment.employee = employee;

      activatedRoute.data = of({ salaryPayment });
      comp.ngOnInit();

      expect(comp.employeesSharedCollection).toContainEqual(employee);
      expect(comp.salaryPayment).toEqual(salaryPayment);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISalaryPayment>>();
      const salaryPayment = { id: 29431 };
      jest.spyOn(salaryPaymentFormService, 'getSalaryPayment').mockReturnValue(salaryPayment);
      jest.spyOn(salaryPaymentService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ salaryPayment });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: salaryPayment }));
      saveSubject.complete();

      // THEN
      expect(salaryPaymentFormService.getSalaryPayment).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(salaryPaymentService.update).toHaveBeenCalledWith(expect.objectContaining(salaryPayment));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISalaryPayment>>();
      const salaryPayment = { id: 29431 };
      jest.spyOn(salaryPaymentFormService, 'getSalaryPayment').mockReturnValue({ id: null });
      jest.spyOn(salaryPaymentService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ salaryPayment: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: salaryPayment }));
      saveSubject.complete();

      // THEN
      expect(salaryPaymentFormService.getSalaryPayment).toHaveBeenCalled();
      expect(salaryPaymentService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISalaryPayment>>();
      const salaryPayment = { id: 29431 };
      jest.spyOn(salaryPaymentService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ salaryPayment });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(salaryPaymentService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareEmployee', () => {
      it('should forward to employeeService', () => {
        const entity = { id: 1749 };
        const entity2 = { id: 1545 };
        jest.spyOn(employeeService, 'compareEmployee');
        comp.compareEmployee(entity, entity2);
        expect(employeeService.compareEmployee).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});

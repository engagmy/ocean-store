import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { DailyCashReconciliationService } from '../service/daily-cash-reconciliation.service';
import { IDailyCashReconciliation } from '../daily-cash-reconciliation.model';
import { DailyCashReconciliationFormService } from './daily-cash-reconciliation-form.service';

import { DailyCashReconciliationUpdateComponent } from './daily-cash-reconciliation-update.component';

describe('DailyCashReconciliation Management Update Component', () => {
  let comp: DailyCashReconciliationUpdateComponent;
  let fixture: ComponentFixture<DailyCashReconciliationUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let dailyCashReconciliationFormService: DailyCashReconciliationFormService;
  let dailyCashReconciliationService: DailyCashReconciliationService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [DailyCashReconciliationUpdateComponent],
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
      .overrideTemplate(DailyCashReconciliationUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(DailyCashReconciliationUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    dailyCashReconciliationFormService = TestBed.inject(DailyCashReconciliationFormService);
    dailyCashReconciliationService = TestBed.inject(DailyCashReconciliationService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const dailyCashReconciliation: IDailyCashReconciliation = { id: 22744 };

      activatedRoute.data = of({ dailyCashReconciliation });
      comp.ngOnInit();

      expect(comp.dailyCashReconciliation).toEqual(dailyCashReconciliation);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDailyCashReconciliation>>();
      const dailyCashReconciliation = { id: 26030 };
      jest.spyOn(dailyCashReconciliationFormService, 'getDailyCashReconciliation').mockReturnValue(dailyCashReconciliation);
      jest.spyOn(dailyCashReconciliationService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ dailyCashReconciliation });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: dailyCashReconciliation }));
      saveSubject.complete();

      // THEN
      expect(dailyCashReconciliationFormService.getDailyCashReconciliation).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(dailyCashReconciliationService.update).toHaveBeenCalledWith(expect.objectContaining(dailyCashReconciliation));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDailyCashReconciliation>>();
      const dailyCashReconciliation = { id: 26030 };
      jest.spyOn(dailyCashReconciliationFormService, 'getDailyCashReconciliation').mockReturnValue({ id: null });
      jest.spyOn(dailyCashReconciliationService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ dailyCashReconciliation: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: dailyCashReconciliation }));
      saveSubject.complete();

      // THEN
      expect(dailyCashReconciliationFormService.getDailyCashReconciliation).toHaveBeenCalled();
      expect(dailyCashReconciliationService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDailyCashReconciliation>>();
      const dailyCashReconciliation = { id: 26030 };
      jest.spyOn(dailyCashReconciliationService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ dailyCashReconciliation });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(dailyCashReconciliationService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IDailyCashReconciliation } from 'app/entities/daily-cash-reconciliation/daily-cash-reconciliation.model';
import { DailyCashReconciliationService } from 'app/entities/daily-cash-reconciliation/service/daily-cash-reconciliation.service';
import { DailyCashDetailService } from '../service/daily-cash-detail.service';
import { IDailyCashDetail } from '../daily-cash-detail.model';
import { DailyCashDetailFormService } from './daily-cash-detail-form.service';

import { DailyCashDetailUpdateComponent } from './daily-cash-detail-update.component';

describe('DailyCashDetail Management Update Component', () => {
  let comp: DailyCashDetailUpdateComponent;
  let fixture: ComponentFixture<DailyCashDetailUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let dailyCashDetailFormService: DailyCashDetailFormService;
  let dailyCashDetailService: DailyCashDetailService;
  let dailyCashReconciliationService: DailyCashReconciliationService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [DailyCashDetailUpdateComponent],
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
      .overrideTemplate(DailyCashDetailUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(DailyCashDetailUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    dailyCashDetailFormService = TestBed.inject(DailyCashDetailFormService);
    dailyCashDetailService = TestBed.inject(DailyCashDetailService);
    dailyCashReconciliationService = TestBed.inject(DailyCashReconciliationService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call DailyCashReconciliation query and add missing value', () => {
      const dailyCashDetail: IDailyCashDetail = { id: 13376 };
      const dailyCashReconciliation: IDailyCashReconciliation = { id: 26030 };
      dailyCashDetail.dailyCashReconciliation = dailyCashReconciliation;

      const dailyCashReconciliationCollection: IDailyCashReconciliation[] = [{ id: 26030 }];
      jest
        .spyOn(dailyCashReconciliationService, 'query')
        .mockReturnValue(of(new HttpResponse({ body: dailyCashReconciliationCollection })));
      const additionalDailyCashReconciliations = [dailyCashReconciliation];
      const expectedCollection: IDailyCashReconciliation[] = [...additionalDailyCashReconciliations, ...dailyCashReconciliationCollection];
      jest.spyOn(dailyCashReconciliationService, 'addDailyCashReconciliationToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ dailyCashDetail });
      comp.ngOnInit();

      expect(dailyCashReconciliationService.query).toHaveBeenCalled();
      expect(dailyCashReconciliationService.addDailyCashReconciliationToCollectionIfMissing).toHaveBeenCalledWith(
        dailyCashReconciliationCollection,
        ...additionalDailyCashReconciliations.map(expect.objectContaining),
      );
      expect(comp.dailyCashReconciliationsSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const dailyCashDetail: IDailyCashDetail = { id: 13376 };
      const dailyCashReconciliation: IDailyCashReconciliation = { id: 26030 };
      dailyCashDetail.dailyCashReconciliation = dailyCashReconciliation;

      activatedRoute.data = of({ dailyCashDetail });
      comp.ngOnInit();

      expect(comp.dailyCashReconciliationsSharedCollection).toContainEqual(dailyCashReconciliation);
      expect(comp.dailyCashDetail).toEqual(dailyCashDetail);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDailyCashDetail>>();
      const dailyCashDetail = { id: 16183 };
      jest.spyOn(dailyCashDetailFormService, 'getDailyCashDetail').mockReturnValue(dailyCashDetail);
      jest.spyOn(dailyCashDetailService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ dailyCashDetail });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: dailyCashDetail }));
      saveSubject.complete();

      // THEN
      expect(dailyCashDetailFormService.getDailyCashDetail).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(dailyCashDetailService.update).toHaveBeenCalledWith(expect.objectContaining(dailyCashDetail));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDailyCashDetail>>();
      const dailyCashDetail = { id: 16183 };
      jest.spyOn(dailyCashDetailFormService, 'getDailyCashDetail').mockReturnValue({ id: null });
      jest.spyOn(dailyCashDetailService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ dailyCashDetail: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: dailyCashDetail }));
      saveSubject.complete();

      // THEN
      expect(dailyCashDetailFormService.getDailyCashDetail).toHaveBeenCalled();
      expect(dailyCashDetailService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDailyCashDetail>>();
      const dailyCashDetail = { id: 16183 };
      jest.spyOn(dailyCashDetailService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ dailyCashDetail });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(dailyCashDetailService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareDailyCashReconciliation', () => {
      it('should forward to dailyCashReconciliationService', () => {
        const entity = { id: 26030 };
        const entity2 = { id: 22744 };
        jest.spyOn(dailyCashReconciliationService, 'compareDailyCashReconciliation');
        comp.compareDailyCashReconciliation(entity, entity2);
        expect(dailyCashReconciliationService.compareDailyCashReconciliation).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});

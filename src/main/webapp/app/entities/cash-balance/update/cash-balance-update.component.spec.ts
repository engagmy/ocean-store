import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { CashBalanceService } from '../service/cash-balance.service';
import { ICashBalance } from '../cash-balance.model';
import { CashBalanceFormService } from './cash-balance-form.service';

import { CashBalanceUpdateComponent } from './cash-balance-update.component';

describe('CashBalance Management Update Component', () => {
  let comp: CashBalanceUpdateComponent;
  let fixture: ComponentFixture<CashBalanceUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let cashBalanceFormService: CashBalanceFormService;
  let cashBalanceService: CashBalanceService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [CashBalanceUpdateComponent],
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
      .overrideTemplate(CashBalanceUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CashBalanceUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    cashBalanceFormService = TestBed.inject(CashBalanceFormService);
    cashBalanceService = TestBed.inject(CashBalanceService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const cashBalance: ICashBalance = { id: 17265 };

      activatedRoute.data = of({ cashBalance });
      comp.ngOnInit();

      expect(comp.cashBalance).toEqual(cashBalance);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICashBalance>>();
      const cashBalance = { id: 21194 };
      jest.spyOn(cashBalanceFormService, 'getCashBalance').mockReturnValue(cashBalance);
      jest.spyOn(cashBalanceService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ cashBalance });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: cashBalance }));
      saveSubject.complete();

      // THEN
      expect(cashBalanceFormService.getCashBalance).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(cashBalanceService.update).toHaveBeenCalledWith(expect.objectContaining(cashBalance));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICashBalance>>();
      const cashBalance = { id: 21194 };
      jest.spyOn(cashBalanceFormService, 'getCashBalance').mockReturnValue({ id: null });
      jest.spyOn(cashBalanceService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ cashBalance: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: cashBalance }));
      saveSubject.complete();

      // THEN
      expect(cashBalanceFormService.getCashBalance).toHaveBeenCalled();
      expect(cashBalanceService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICashBalance>>();
      const cashBalance = { id: 21194 };
      jest.spyOn(cashBalanceService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ cashBalance });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(cashBalanceService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});

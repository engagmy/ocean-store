import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { CashTransactionService } from '../service/cash-transaction.service';
import { ICashTransaction } from '../cash-transaction.model';
import { CashTransactionFormService } from './cash-transaction-form.service';

import { CashTransactionUpdateComponent } from './cash-transaction-update.component';

describe('CashTransaction Management Update Component', () => {
  let comp: CashTransactionUpdateComponent;
  let fixture: ComponentFixture<CashTransactionUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let cashTransactionFormService: CashTransactionFormService;
  let cashTransactionService: CashTransactionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [CashTransactionUpdateComponent],
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
      .overrideTemplate(CashTransactionUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CashTransactionUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    cashTransactionFormService = TestBed.inject(CashTransactionFormService);
    cashTransactionService = TestBed.inject(CashTransactionService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const cashTransaction: ICashTransaction = { id: 32174 };

      activatedRoute.data = of({ cashTransaction });
      comp.ngOnInit();

      expect(comp.cashTransaction).toEqual(cashTransaction);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICashTransaction>>();
      const cashTransaction = { id: 22223 };
      jest.spyOn(cashTransactionFormService, 'getCashTransaction').mockReturnValue(cashTransaction);
      jest.spyOn(cashTransactionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ cashTransaction });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: cashTransaction }));
      saveSubject.complete();

      // THEN
      expect(cashTransactionFormService.getCashTransaction).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(cashTransactionService.update).toHaveBeenCalledWith(expect.objectContaining(cashTransaction));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICashTransaction>>();
      const cashTransaction = { id: 22223 };
      jest.spyOn(cashTransactionFormService, 'getCashTransaction').mockReturnValue({ id: null });
      jest.spyOn(cashTransactionService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ cashTransaction: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: cashTransaction }));
      saveSubject.complete();

      // THEN
      expect(cashTransactionFormService.getCashTransaction).toHaveBeenCalled();
      expect(cashTransactionService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICashTransaction>>();
      const cashTransaction = { id: 22223 };
      jest.spyOn(cashTransactionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ cashTransaction });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(cashTransactionService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});

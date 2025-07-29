import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { BillService } from '../service/bill.service';
import { IBill } from '../bill.model';
import { BillFormService } from './bill-form.service';

import { BillUpdateComponent } from './bill-update.component';

describe('Bill Management Update Component', () => {
  let comp: BillUpdateComponent;
  let fixture: ComponentFixture<BillUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let billFormService: BillFormService;
  let billService: BillService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [BillUpdateComponent],
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
      .overrideTemplate(BillUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(BillUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    billFormService = TestBed.inject(BillFormService);
    billService = TestBed.inject(BillService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const bill: IBill = { id: 14455 };

      activatedRoute.data = of({ bill });
      comp.ngOnInit();

      expect(comp.bill).toEqual(bill);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBill>>();
      const bill = { id: 8530 };
      jest.spyOn(billFormService, 'getBill').mockReturnValue(bill);
      jest.spyOn(billService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ bill });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: bill }));
      saveSubject.complete();

      // THEN
      expect(billFormService.getBill).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(billService.update).toHaveBeenCalledWith(expect.objectContaining(bill));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBill>>();
      const bill = { id: 8530 };
      jest.spyOn(billFormService, 'getBill').mockReturnValue({ id: null });
      jest.spyOn(billService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ bill: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: bill }));
      saveSubject.complete();

      // THEN
      expect(billFormService.getBill).toHaveBeenCalled();
      expect(billService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBill>>();
      const bill = { id: 8530 };
      jest.spyOn(billService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ bill });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(billService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});

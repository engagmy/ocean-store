import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { OutgoingPaymentService } from '../service/outgoing-payment.service';
import { IOutgoingPayment } from '../outgoing-payment.model';
import { OutgoingPaymentFormService } from './outgoing-payment-form.service';

import { OutgoingPaymentUpdateComponent } from './outgoing-payment-update.component';

describe('OutgoingPayment Management Update Component', () => {
  let comp: OutgoingPaymentUpdateComponent;
  let fixture: ComponentFixture<OutgoingPaymentUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let outgoingPaymentFormService: OutgoingPaymentFormService;
  let outgoingPaymentService: OutgoingPaymentService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [OutgoingPaymentUpdateComponent],
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
      .overrideTemplate(OutgoingPaymentUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(OutgoingPaymentUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    outgoingPaymentFormService = TestBed.inject(OutgoingPaymentFormService);
    outgoingPaymentService = TestBed.inject(OutgoingPaymentService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const outgoingPayment: IOutgoingPayment = { id: 18513 };

      activatedRoute.data = of({ outgoingPayment });
      comp.ngOnInit();

      expect(comp.outgoingPayment).toEqual(outgoingPayment);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IOutgoingPayment>>();
      const outgoingPayment = { id: 29929 };
      jest.spyOn(outgoingPaymentFormService, 'getOutgoingPayment').mockReturnValue(outgoingPayment);
      jest.spyOn(outgoingPaymentService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ outgoingPayment });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: outgoingPayment }));
      saveSubject.complete();

      // THEN
      expect(outgoingPaymentFormService.getOutgoingPayment).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(outgoingPaymentService.update).toHaveBeenCalledWith(expect.objectContaining(outgoingPayment));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IOutgoingPayment>>();
      const outgoingPayment = { id: 29929 };
      jest.spyOn(outgoingPaymentFormService, 'getOutgoingPayment').mockReturnValue({ id: null });
      jest.spyOn(outgoingPaymentService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ outgoingPayment: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: outgoingPayment }));
      saveSubject.complete();

      // THEN
      expect(outgoingPaymentFormService.getOutgoingPayment).toHaveBeenCalled();
      expect(outgoingPaymentService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IOutgoingPayment>>();
      const outgoingPayment = { id: 29929 };
      jest.spyOn(outgoingPaymentService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ outgoingPayment });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(outgoingPaymentService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});

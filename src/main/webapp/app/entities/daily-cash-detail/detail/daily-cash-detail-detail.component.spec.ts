import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { DailyCashDetailDetailComponent } from './daily-cash-detail-detail.component';

describe('DailyCashDetail Management Detail Component', () => {
  let comp: DailyCashDetailDetailComponent;
  let fixture: ComponentFixture<DailyCashDetailDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DailyCashDetailDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./daily-cash-detail-detail.component').then(m => m.DailyCashDetailDetailComponent),
              resolve: { dailyCashDetail: () => of({ id: 16183 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(DailyCashDetailDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DailyCashDetailDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load dailyCashDetail on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', DailyCashDetailDetailComponent);

      // THEN
      expect(instance.dailyCashDetail()).toEqual(expect.objectContaining({ id: 16183 }));
    });
  });

  describe('PreviousState', () => {
    it('should navigate to previous state', () => {
      jest.spyOn(window.history, 'back');
      comp.previousState();
      expect(window.history.back).toHaveBeenCalled();
    });
  });
});

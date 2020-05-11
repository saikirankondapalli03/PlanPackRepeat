import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ItineraryBaseComponent } from './itinerary-base.component';

describe('ItineraryBaseComponent', () => {
  let component: ItineraryBaseComponent;
  let fixture: ComponentFixture<ItineraryBaseComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ItineraryBaseComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ItineraryBaseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

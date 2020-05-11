import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SearchItineraryComponent } from './search-itinerary.component';

describe('SearchItineraryComponent', () => {
  let component: SearchItineraryComponent;
  let fixture: ComponentFixture<SearchItineraryComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SearchItineraryComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SearchItineraryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

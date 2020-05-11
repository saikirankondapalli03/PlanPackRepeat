import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { HomePageCarouselViewComponent } from './home-page-carousel-view.component';

describe('HomePageCarouselViewComponent', () => {
  let component: HomePageCarouselViewComponent;
  let fixture: ComponentFixture<HomePageCarouselViewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ HomePageCarouselViewComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(HomePageCarouselViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DistinationsComponent } from './distinations.component';

describe('DistinationsComponent', () => {
  let component: DistinationsComponent;
  let fixture: ComponentFixture<DistinationsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DistinationsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DistinationsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

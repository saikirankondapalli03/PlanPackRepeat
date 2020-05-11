import { TestBed } from '@angular/core/testing';

import { CarouselServiceService } from './carousel-view.service';

describe('CarouselServiceService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: CarouselServiceService = TestBed.get(CarouselServiceService);
    expect(service).toBeTruthy();
  });
});

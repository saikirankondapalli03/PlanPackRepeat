import { Component, OnInit, OnDestroy } from '@angular/core';
import { ItineraryService } from '../itinerary.service';
import { Itinerary } from '../../models/itinerary';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-summary',
  templateUrl: './summary.component.html',
  styleUrls: ['./summary.component.scss']
})
export class SummaryComponent implements OnInit, OnDestroy {

  itineraryobj: Itinerary;
  subscriptions = new Subscription();

  constructor(private itineraryService: ItineraryService) { }

  ngOnInit() {
    this.init();
  }

  init(): void {
    this.subscriptions.add(this.itineraryService.itineraryStream.subscribe(
      (data: Itinerary) => {
        console.log('These are the updates received: summary', data);
        this.itineraryobj = data;
      }
    ));
  }

  ngOnDestroy() {
    this.subscriptions.unsubscribe();
  }

}

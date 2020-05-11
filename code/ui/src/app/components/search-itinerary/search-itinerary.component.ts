import { Component, OnInit, OnChanges, SimpleChange } from '@angular/core';
import { MessageService } from 'src/app/services/message.service';

@Component({
  selector: 'app-search-itinerary',
  templateUrl: './search-itinerary.component.html',
  styleUrls: ['./search-itinerary.component.css']
})
export class SearchItineraryComponent implements OnChanges {
  searchText: any;
  constructor(private messageService: MessageService) { }

  ngOnInit() {

  }
  changeLog: string[] = [];
  ngOnChanges(changes: {[propKey: string]: SimpleChange}) {
    let log: string[] = [];
    for (let propName in changes) {
      let changedProp = changes[propName];
      let to = JSON.stringify(changedProp.currentValue);
      if (changedProp.isFirstChange()) {
        log.push(`Initial value of ${propName} set to ${to}`);
      } else {
        let from = JSON.stringify(changedProp.previousValue);
        log.push(`${propName} changed from ${from} to ${to}`);
      }
    }
    this.changeLog.push(log.join(', '));
  }

  doSomething(event){
    console.log("event sent is ===> ",event)
    this.messageService.sendMessage(event);
  }
}

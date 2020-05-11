import { Component, OnInit, OnDestroy, ViewChild, Input, Output, EventEmitter, AfterViewInit } from '@angular/core';
import { ReplaySubject, Subscription } from 'rxjs';
import { ItineraryService } from '../../itinerary/itinerary.service';

@Component({
  selector: 'app-dialog',
  templateUrl: './dialog.component.html',
  styleUrls: ['./dialog.component.scss']
})

export class DialogComponent implements OnInit, AfterViewInit, OnDestroy {

  @Output() openDialogEvent = new EventEmitter<any>();
  @Input() content: string;

  @ViewChild('googleDialog', {static: false}) googleDialog: any;

  componentSubscription = new Subscription();

  public dialogRef: ReplaySubject<boolean> = new ReplaySubject(1);
  dialogRefStream = this.dialogRef.asObservable();

  googleDialogIsOpen = false;

  constructor(private itineraryService: ItineraryService) { }

  ngOnInit() {
    this.componentSubscription.add(this.dialogRefStream.subscribe((data: any) => {
      this.googleDialogIsOpen = data;
    }));
  }

  ngAfterViewInit(): void {
    this.openDialogEvent.emit(this.dialogRef);
  }

  saveMapsDestinations(): void {
    this.itineraryService.onSaveMapsLocationsSubject.next(true);
  }

  ngOnDestroy() {
    this.componentSubscription.unsubscribe();
  }

}

/// <reference types='@types/googlemaps' />
import { Component, OnInit, ViewChild, ElementRef, NgZone, Input, OnDestroy } from '@angular/core';
import { MapsAPILoader, MouseEvent, PolylineManager, GoogleMapsAPIWrapper } from '@agm/core';
import { LoadingService } from '../../shared/loading/loading.service';
import { ItineraryService } from '../../itinerary/itinerary.service';
import { Observable, Subject, ReplaySubject, Subscription } from 'rxjs';

@Component({
  selector: 'app-google-maps',
  templateUrl: './google-maps.component.html',
  styleUrls: ['./google-maps.component.scss'],
  providers: [PolylineManager, GoogleMapsAPIWrapper],
})
export class GoogleMapsComponent implements OnInit, OnDestroy {
  selectedLocations = [];
  selectedMapCoordinates = [];

  title = 'AGM project';
  latitude: number;
  longitude: number;
  zoom: number;
  address: string;
  mapLoaded: boolean;
  componentSubscription = new Subscription();
  private geoCoder: google.maps.Geocoder;

  @ViewChild('search', { static: false }) searchElementRef: ElementRef;
  @Input() dialogRef: ReplaySubject<any>;

  constructor(
    private mapsAPILoader: MapsAPILoader,
    private ngZone: NgZone,
    private loadingService: LoadingService,
    private itineraryService: ItineraryService
  ) {}

  ngOnInit() {
    this.initOnSaveEventSubscription();
    this.loadGoogleMaps();
  }

  initOnSaveEventSubscription(): void {
    this.componentSubscription.add(this.itineraryService.onSaveMapsLocationsStream.subscribe(
      (data: boolean) => {
        if (!!data) {
          this.loadingService.enableLoadingMask('Saving');
          console.log(
            'The itinerary has been saved, fetching street address and pix now.',
            data
          );
          if (this.selectedLocations.length > 0) {
            this.itineraryService.savedDestinations = this.selectedLocations;
          }

          this.loadingService.disableLoadingMask();
          this.dialogRef.next(false);
        }
      },
      (error: any) => {
        console.log('Error occurred on save event.', error);
        this.loadingService.disableLoadingMask();
      }
    ));
  }

  loadGoogleMaps(): void {
    this.loadingService.enableLoadingMask('Loading Google Maps');
    this.mapsAPILoader.load().then(() => {
      this.setCurrentLocation();
      this.geoCoder = new google.maps.Geocoder();

      const autocomplete = new google.maps.places.Autocomplete(
        this.searchElementRef.nativeElement,
        {
          types: ['address'],
        }
      );
      autocomplete.addListener('place_changed', () => {
        this.ngZone.run(() => {
          // get the place result
          const place: google.maps.places.PlaceResult = autocomplete.getPlace();
          // verify result
          if (place.geometry === undefined || place.geometry === null) {
            return;
          }
          // set latitude, longitude and zoom
          this.latitude = place.geometry.location.lat();
          this.longitude = place.geometry.location.lng();
          this.zoom = 15;
          this.address = place.formatted_address;
        });
      });
    });
  }

  // Get Current Location Coordinates
  private setCurrentLocation() {
    if ('geolocation' in navigator) {
      navigator.geolocation.getCurrentPosition((position) => {
        this.latitude = position.coords.latitude;
        this.longitude = position.coords.longitude;
        this.zoom = 8;
        this.getAddress(this.latitude, this.longitude);
      });
    }
  }

  markerDragEnd($event: MouseEvent) {
    console.log($event);
    this.latitude = $event.coords.lat;
    this.longitude = $event.coords.lng;
    this.getAddress(this.latitude, this.longitude);
  }

  getAddress(latitude: number, longitude: number) {
    this.geoCoder.geocode(
      { location: { lat: latitude, lng: longitude } },
      (results, status) => {
        if (status === google.maps.GeocoderStatus.OK) {
          if (results[0]) {
            this.zoom = 12;
            this.address = results[0].formatted_address;
            this.loadingService.disableLoadingMask();
          } else {
            console.error('No results found');
            this.loadingService.disableLoadingMask();
          }
        } else {
          console.error('Geocoder failed due to: ' + status);
          this.loadingService.disableLoadingMask();
        }
      }
    );
  }

  getAddressFromCoordinates(
    latitude: number,
    longitude: number
  ): Observable<any> {
    const streetAddressSubject = new Subject<any>();
    const location = new google.maps.LatLng(latitude, longitude);
    this.geoCoder.geocode({ location }, (results, status) => {
        if (status === google.maps.GeocoderStatus.OK) {
          const address = this.getStreetAddress(results);
          streetAddressSubject.next(address);
          return address;
        } else {
          console.error('Geocoder failed due to: ' + status);
          streetAddressSubject.next(null);
        }
      }
    );
    return streetAddressSubject.asObservable();
  }

  getStreetAddress(locationResults: Array<any>) {
    let locationStreetAddress: string;
    if (locationResults.length > 0) {
      for (const location of locationResults) {
        if (location.types.indexOf('street_address') > -1) {
          locationStreetAddress = location.formatted_address;
        }
      }
    }
    return locationStreetAddress;
  }

  selectLocation(event: any) {
    let currentAddress = {};
    this.selectedMapCoordinates.push({
      latitude: event.coords.lat,
      longitude: event.coords.lng,
    });
    this.componentSubscription.add(this.getAddressFromCoordinates(
      event.coords.lat,
      event.coords.lng
    ).subscribe((response: string | null) => {
      const streetAddress = response;
      currentAddress = {
        latitude: event.coords.lat,
        longitude: event.coords.lng,
        streetAddress,
        source: 'maps',
        name: '',
        date: '',
        time: '',
        budget: 0
      };
      this.selectedLocations.push(currentAddress);
    }));
  }

  removeLocation(location: any) {
    const index = this.itineraryService.savedDestinations.indexOf(location);
    if (typeof index !== 'undefined' || index !== null) {
      this.itineraryService.savedDestinations.splice(index, 1);
    }
  }

  get savedDestinations() {
    return this.itineraryService.savedDestinations;
  }

  ngOnDestroy() {
    this.componentSubscription.unsubscribe();
  }
}

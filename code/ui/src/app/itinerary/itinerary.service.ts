import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { BehaviorSubject, Observable, ReplaySubject } from 'rxjs';
import { Itinerary, Destinations, BackendDestination, BackendItinerary } from '../models/itinerary';
import { UserService } from '../services/user.service';

@Injectable({
  providedIn: 'root'
})
export class ItineraryService {

  private itinerarySubject: ReplaySubject<Itinerary> = new ReplaySubject();
  public onSaveMapsLocationsSubject: ReplaySubject<boolean> = new ReplaySubject();

  itineraryStream = this.itinerarySubject.asObservable();
  onSaveMapsLocationsStream = this.onSaveMapsLocationsSubject.asObservable();

  savedDestinations: Array<Destinations> = [];
  editItinerary: boolean;

  trackerOptions = [
    {
      step: 'info',
      displayName: 'info',
      stepReady: true,
      section: 'info-progress',
      stepCompleted: false,
      showStep: true
    },
    {
      step: 'destinations',
      displayName: 'destinations',
      stepReady: false,
      section: 'destinations-progress',
      stepCompleted: false,
      showStep: true
    },
    {
      step: 'budget',
      displayName: 'budget & pictures',
      stepReady: false,
      section: 'budget-progress',
      stepCompleted: false,
      showStep: true
    },
    {
      step: 'submit',
      stepReady: false,
      section: 'submit-progress',
      stepCompleted: false,
      showStep: false
    }
  ];

  itineraryObj: Itinerary;

  updateTrackerOptions(isSuperUser) {
    console.log('Tracker options have been called:', this.trackerOptions);
    if (this.itineraryObj) {
    for (const option of this.trackerOptions) {

      // info
      if (option.step === 'info' && this.itineraryObj.info) {
        if (this.itineraryObj.info.name &&
          this.itineraryObj.info.startDate &&
          this.itineraryObj.info.endDate &&
          this.itineraryObj.info.visiblity) {
            option.stepCompleted = true;
            option.stepReady = true;
          }
      }

      if (option.step === 'destinations' && this.itineraryObj.destinations) {
        let isValid = false;
        for (const destination of this.itineraryObj.destinations) {
          if (destination.date !== "" &&
              destination.name &&
              destination.source &&
              destination.streetAddress !== '' &&
              destination.time !== '' &&
              (isSuperUser && destination.budget > -1) || (!isSuperUser)) {
                isValid = true;
              } else {
                isValid = false;
                break;
              }
        }
        option.stepCompleted = isValid;
        option.stepReady = true;
      }

      if (option.step === 'budget' && this.itineraryObj.budget) {
        if (this.itineraryObj.budget > 0) {
          option.stepCompleted = true;
          option.stepReady = true;
        }

      }
    }
  }
}

  constructor(private httpClient: HttpClient, private userService: UserService) {
   }

  isDestinationPageValid(): boolean {
    for (const option of this.trackerOptions) {
      if (option.step === 'destinations') {
        return option.stepCompleted;
      }
    }
  }

  broadcastUpdates(itineraryData: any): void {
    this.itineraryObj = itineraryData;
    this.itinerarySubject.next(itineraryData);
  }

  saveItinerary(): Observable<any> {
    const payload: BackendItinerary = {
      itineraryName: this.itineraryObj.info.name,
      startDate: this.itineraryObj.info.startDate,
      endDate: this.itineraryObj.info.endDate,
      email: this.userService.userEmail,
      budgetId: this.itineraryObj.budget,
      destinations: [],
      active: true,
      public: this.itineraryObj.info.visiblity === 'Public' ? true : false,
      pictures: this.itineraryObj.photos ? Array.from(this.itineraryObj.photos) : [],
      visibilityKey: `p${Date.now().toString()}x`
    };

    this.itineraryObj.destinations.forEach((destination: Destinations) => {
      const dest: BackendDestination = {
        destName: destination.name,
        address: destination.streetAddress,
        plannedTime: `${destination.date} ${destination.time}:00`,
        status: destination.status,
        imgUrl: '',
        latitude: destination.latitude ? destination.latitude.toString() : null,
        longitude: destination.longitude ? destination.longitude.toString() : null,
        source: destination.source,
        budget: destination.budget
      };
      payload.destinations.push(dest);
    });
    return this.httpClient.post(`https://travelapp-boot.cfapps.io/itinerary/createItinerary`, payload);
  }


  getUiItineraryModelFromRaw(rawItineraryObj: any) {
    const itineraryObjTemp =  {
      info: {
        name: rawItineraryObj.itineraryName,
        startDate: this.getDateHtmlDate(rawItineraryObj.startDate),
        endDate: this.getDateHtmlDate(rawItineraryObj.endDate),
        visiblity: rawItineraryObj.public ? 'Public' : 'Private',
      },
      destinations: [],
      budget: rawItineraryObj.budgetId
    };

    rawItineraryObj.destinations.forEach((destination) => {
      const destObj: Destinations = {
        name: destination.destName,
        streetAddress: destination.address,
        date: this.getDateHtmlDate(destination.plannedTime),
        time: this.getDateHtmlTime(destination.plannedTime),
        latitude: destination.latitude,
        longitude: destination.longitude,
        status: destination.status,
        source: destination.source
      };
      itineraryObjTemp.destinations.push(destObj);
    });
    return itineraryObjTemp;
  }

  getItineraryDetails(itineraryId: string) {
    return this.httpClient.get(`https://travelapp-boot.cfapps.io/itinerary/getItineraryById/${itineraryId}`);
  }

  getDateHtmlDate(date): string {
    const dateObj = new Date(date);
    return dateObj.getFullYear() + '-' + dateObj.getMonth() + '-' + dateObj.getDate();
  }

  getDateHtmlTime(date): string {
    const dateObj = new Date(date);
    return dateObj.getHours() + ':' + dateObj.getMinutes();
  }

  updateItinerary(itineraryId: string, payload: any): Observable<any> {
    return this.httpClient.put(`https://travelapp-boot.cfapps.io/itinerary/${itineraryId}`, payload);
  }

  likeItinerary(itineraryId: string, email: string) {
    return this.httpClient.post(`https://travelapp-boot.cfapps.io/notification/saveUserLiking/${email}/${itineraryId}?email=${email}&itineraryId=${itineraryId}`, {});
  }

  unlikeItinerary(itineraryId: string, email: string) {
    return this.httpClient.post(`https://travelapp-boot.cfapps.io/notification/saveUserUnLiking/${email}/${itineraryId}?email=${email}&itineraryId=${itineraryId}`, {});
  }

  getItineraryLikes(itineraryId: string) {
    return this.httpClient.get(`https://travelapp-boot.cfapps.io/notification/getUsersLikeCountByItinerary/${itineraryId}`);
  }
  getUserItineraries(userEmail: string): Observable<any> {
    // tslint:disable-next-line: max-line-length
    return this.httpClient.get(`https://travelapp-boot.cfapps.io/itinerary/getItineraryByEmail/${userEmail}`);
  }

  getAllItineraries(): Observable<any> {
    // tslint:disable-next-line: max-line-length
    return this.httpClient.get(`https://travelapp-boot.cfapps.io/itinerary/getAllItineraries`);
  }


}

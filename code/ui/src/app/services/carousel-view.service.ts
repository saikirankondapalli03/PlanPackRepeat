import { Itinerary, Destination } from '../models/carouselmodels';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable , of } from 'rxjs';

import {jsonString} from './../../mockjson/retrieveitinerary';
@Injectable({
  providedIn: 'root'
})

export class CarouselViewService {

  constructor(private authHttp: HttpClient) { }

  getItineraryInfoDashboard(email: string, isJsonString?: boolean): Observable<any> {
      // return this.authHttp.get('./../../mockjson/retrieveitinerary.json')
      if (isJsonString) {
        console.log("printing json string")
        console.log(jsonString)
        return of(jsonString);
      } else {
        return this.authHttp.get(`https://travelapp-boot.cfapps.io/users/getUserByEmail/saransh%40gmail.com`);
      }

  }


}

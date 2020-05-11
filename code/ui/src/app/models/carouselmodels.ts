export class Itinerary {
  constructor(private itineraryId?: String , private itineraryName?: String, private pictures?: Array<string>, private destinations?: Destination[] ) {

  }

  getItineraryName(){
    return this.itineraryName;
  }
}

export class Destination{
  constructor(private destinationName?:String , private imageSourceUrl ?: String) {

 }
}

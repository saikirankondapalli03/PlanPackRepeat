export interface HomeItinerary {
    itineraryId: string;
    itineraryName: string;
    destinations?: (DestinationsEntity)[] | null;
  }

export interface DestinationsEntity {
    destinationName: string;
    imageSourceUrl: string;
}

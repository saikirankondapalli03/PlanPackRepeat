import { PagerService } from './../../services/pager.service';

import { Itinerary, Destination } from './../../models/carouselmodels';
import { CarouselViewService } from './../../services/carousel-view.service';
import { LoadingService } from '../../shared/loading/loading.service';
import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { UserService } from '../../services/user.service';
import { ItineraryService } from '../../itinerary/itinerary.service';
import { ToastrService } from 'ngx-toastr';
import * as _ from 'lodash';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-home-page-carousel-view',
  templateUrl: './home-page-carousel-view.component.html',
  styleUrls: ['./home-page-carousel-view.component.scss']
})
export class HomePageCarouselViewComponent implements OnInit, OnDestroy {

  filteredItinerary: Itinerary[] = [];
  pager: any = {};
  pagedItems: any[];
  userItineraries: Array<any> = [];
  activeTab: 'My' | 'All' = 'All'
  allitineraries: Itinerary[] = [];
  componentSubscriptions = new Subscription();

  constructor(
    private carouselService: CarouselViewService,
    private pagerService: PagerService,
    private userService: UserService,
    private itineraryService: ItineraryService,
    private router: Router,
    private loadingService: LoadingService,
    private alertService: ToastrService
  ) {}

  ngOnInit() {
    this.getAllUsersItineraries();
  }

  initCarousal() {
    this.componentSubscriptions.add(this.carouselService
      .getItineraryInfoDashboard('saransh@gmail.com', true)
      .subscribe(
        (result: Array<any>) => {
          const value_list = [];
          result.forEach(itinerary => {
            const destArray = [];
            itinerary.destinations.forEach(destination => {
              destArray.push(
                new Destination(destination.destName, destination.imgUrl)
              );
            });
            const iti = new Itinerary(
              itinerary.id,
              itinerary.itineraryName,
              itinerary.pictures,
              destArray
            );
            value_list.push(iti);
          });
          this.allitineraries = value_list;
          this.setPage(1);
          this.getUserItineraries();
        },
        error => {
          console.log('erroor is ', error);
        }
      ));
  }

  getUserItineraries() {
    this.loadingService.enableLoadingMask();
    this.componentSubscriptions.add(this.itineraryService
      .getUserItineraries(this.userService.userEmail)
      .subscribe(
        (data: any) => {
          this.parseRawItineraryModelToUIModel(data);
          this.pagedItems = this.userItineraries.concat(this.pagedItems);
          this.loadingService.disableLoadingMask();
          this.alertService.success('Successfully loaded your itineraries!');
        },
        (error: any) => {
          console.log(
            'Error occurred while fetching the user itineraries:',
            error
          );
          this.loadingService.disableLoadingMask();
          this.alertService.error('Sorry, an error occurred. Please refresh and try again.');
        }
      ));
  }

  getAllUsersItineraries() {
    this.loadingService.enableLoadingMask();
    this.componentSubscriptions.add(this.itineraryService
      .getAllItineraries()
      .subscribe(
        (data: any) => {
          this.parseRawItineraryModelToUIModel(data);
          let allitsTest = _.filter(this.userItineraries, (itin: any) => {
            return itin.public;
          });
          this.pagedItems = allitsTest;
          this.loadingService.disableLoadingMask();
          this.alertService.success('Successfully loaded all itineraries!');
        },
        (error: any) => {
          console.log(
            'Error occurred while fetching the user itineraries:',
            error
          );
          this.loadingService.disableLoadingMask();
          this.alertService.error('Sorry, an error occurred. Please refresh and try again.');
        }
      ));
  }


  setPage(page: number) {
    this.pager = this.pagerService.getPager(this.allitineraries.length, page);
  }

  parseRawItineraryModelToUIModel(userItineraries: Array<any>) {
    userItineraries.forEach((itinerary: any) => {
      const uiHomePageItinObj = {
        itineraryId: itinerary.id,
        itineraryName: itinerary.itineraryName,
        destinations: itinerary.destinations,
        pictures: itinerary.pictures,
        likes: itinerary.likes,
        tripDuration: this.getNumberOfDays(itinerary.startDate, itinerary.endDate),
        public: itinerary.public,
        budgetId: itinerary.budgetId,
        likeList: this.checkNull(itinerary.likesDetails)



      }
      this.userItineraries = this.userItineraries.concat([uiHomePageItinObj]);
    });
    this.userItineraries.sort((a, b) => {
      return a['likes'] - b['likes'];
    });
    this.userItineraries = this.userItineraries.reverse();
  }

checkNull(likesDetails){
  if(likesDetails == null || likesDetails.listOfUsers == null){
    return 0
  } else {
    const listOfUsers = likesDetails.listOfUsers
    return listOfUsers.length
  }

}

getNumberOfDays(date1, date2) {
  const dateObj1 = new Date(date1);
  const dateObj2 = new Date(date2);

  const utc1 = Date.UTC(dateObj1.getFullYear(), dateObj1.getMonth(), dateObj1.getDate());
  const utc2 = Date.UTC(dateObj2.getFullYear(), dateObj2.getMonth(), dateObj2.getDate());
  const result = Math.floor((utc2 - utc1) / (1000 * 60 * 60 * 24));
  if (typeof result === 'number') {
    return result;
  } else {
    return "";
  }
}

getAllItineraries() {
  if (this.activeTab === 'All') {
    return;
  }
  this.activeTab = 'All';
  this.resetItinerariesArrays();
  this.getAllUsersItineraries();
}

getMyItineraries() {
  if (this.activeTab === 'My') {
    return;
  }
  this.activeTab = 'My';
  this.resetItinerariesArrays();
  this.getUserItineraries();
}

resetItinerariesArrays() {
  this.pagedItems = [];
  this.allitineraries = [];
  this.userItineraries = [];
}

goToItineraryDetails(id: string) {
  if (id) {
    this.router.navigateByUrl(`/itinerary/${id}`);
  }
}

ngOnDestroy() {
  this.componentSubscriptions.unsubscribe();
}

}

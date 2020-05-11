import { Component, OnInit, OnDestroy } from '@angular/core';
import { ItineraryService } from '../itinerary.service';
import { Itinerary } from 'src/app/models/itinerary';
import { Router, NavigationEnd, ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { UserService } from '../../services/user.service';
import { AuthService } from 'src/app/auth/auth.service';

@Component({
  selector: "app-progress-tracker",
  templateUrl: "./progress-tracker.component.html",
  styleUrls: ["./progress-tracker.component.scss"],
})
export class ProgressTrackerComponent implements OnInit, OnDestroy {
  itineraryobj: Itinerary;
  orderRoute: string;
  userInfo: any;
  userEmail: string;
  subscriptions = new Subscription();

  constructor(
    private itineraryService: ItineraryService,
    private router: Router,
    private userService: UserService,
    private authService: AuthService
  ) {}

  ngOnInit() {
    this.authService.getUser$().subscribe(
      data => {
        this.userEmail = data.email;
        this.userService.getUserInfo(this.userEmail).subscribe(
          (userInfo) => {
            this.userService.isSuperUser = userInfo.adminUser;
            this.userInfo = userInfo;
            this.init();
          },
          (error) => {
            console.log("Error:", error);
          }
        );
      },
      error => {
        console.log("Error:", error);
      }
    );
    this.initActivateRoute();
  }

  initActivateRoute() {
    this.orderRoute = this.router.url;
    this.subscriptions.add(
      this.router.events.subscribe((val) => {
        if (val instanceof NavigationEnd) {
          this.orderRoute = val.url;
        }
      })
    );
  }

  goToStep(step: string) {
    console.log("This has been called", step);
    this.router.navigateByUrl(`/itinerary/create-itinerary/${step}`);
  }

  init(): void {
    this.subscriptions.add(
      this.itineraryService.itineraryStream.subscribe((data: Itinerary) => {
        console.log("These are the updates received: progress tracker", data);
        this.itineraryobj = data;
        this.itineraryService.updateTrackerOptions(this.userInfo.adminUser);
      })
    );
  }

  get updatedTrackerOptions() {
    return this.itineraryService.trackerOptions;
  }

  ngOnDestroy() {
    this.subscriptions.unsubscribe();
  }
}

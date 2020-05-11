import { Component, OnInit, OnDestroy } from "@angular/core";
import { Router, ActivatedRoute } from "@angular/router";
import { ItineraryService } from "../itinerary.service";
import { Subscription } from "rxjs";
import { LoadingService } from "../../shared/loading/loading.service";
import { UserService } from "../../services/user.service";
import { cloneDeep } from "lodash";
import { ToastrService } from "ngx-toastr";
import { AuthService } from 'src/app/auth/auth.service';

const HOUR_MILLI_SECONDS = 20000;

@Component({
  selector: "app-details",
  templateUrl: "./details.component.html",
  styleUrls: ["./details.component.scss"],
})
export class DetailsComponent implements OnInit, OnDestroy {
  itineraryId: string;
  itineraryDetails: any;
  isLikeRequestPending: boolean;
  userEmail: string;
  userInfo: any;
  errorMessage: string;
  visibilityKey: string;
  visibilityKeyId: string;
  showVisibilityKey = false;
  itineraryLikes: string[] = [];
  subscriptions = new Subscription();

  constructor(
    private alertsService: ToastrService,
    private activatedRoute: ActivatedRoute,
    private itineraryService: ItineraryService,
    private loadingService: LoadingService,
    private userService: UserService,
    private authService: AuthService
  ) {}

  ngOnInit() {
    this.loadingService.enableLoadingMask();
    this.authService.getUser$().subscribe(((userData: any) => {
      this.userEmail = userData.email;
      this.userService.getUserInfo(this.userEmail).subscribe(
        (userInfo) => {
          this.userInfo = userInfo;
          this.userService.isSuperUser = userInfo.adminUser;
          this.getItineraryDetails();
        },
        (error) => {
          console.log("Error:", error);
        }
      )
      }
    ));
    this.subscriptions.add(
      this.activatedRoute.params.subscribe((params: any) => {
        this.itineraryId = params.id;
        this.visibilityKeyId = params.visibilityKey;
        this.subscriptions.add(
          this.itineraryService
            .getItineraryLikes(this.itineraryId)
            .subscribe((likeItiUpdateData: any) => {
              console.log("Itinerary likes:", likeItiUpdateData);
              this.itineraryLikes = likeItiUpdateData.listOfUsers;
            })
        );
      })
    );
  }

  getItineraryDetails() {
    this.subscriptions.add(
      this.itineraryService
        .getItineraryDetails(this.itineraryId)
        .subscribe((data: any) => {
          this.loadingService.disableLoadingMask();
          if (data.email !== this.userEmail && !data.public) {
            if (typeof this.visibilityKeyId === "undefined") {
              this.errorMessage = `This is a private Itinerary. Please request access from the itinerary owner.`;
              return;
            } else {
              this.itineraryDetails = data;
              this.visibilityKey = this.itineraryDetails.visibilityKey;
              if (this.isVisibilityKeyExpired(data.visibilityKey)) {
                this.errorMessage = `The last Visibility Key for this itinerary has been expired. Please request a new one from the itinerary owner.`;
                return;
              } else if (!this.validateVisibilityKey()) {
                this.errorMessage = `Incorrect Visibility Key.`;
                return;
              }
              return;
            }
          }
          this.itineraryDetails = data;
          this.visibilityKey = this.itineraryDetails.visibilityKey;
        })
    );
  }

  isVisibilityKeyExpired(visibilityKeyId) {
    const itineraryVisibilityKey = visibilityKeyId;
    if (itineraryVisibilityKey) {
      const visibilityKeyTimeCode = itineraryVisibilityKey.substring(
        itineraryVisibilityKey.lastIndexOf("p") + 1,
        itineraryVisibilityKey.lastIndexOf("x")
      );
      const timeEllapsed =
        new Date().getTime() - parseInt(visibilityKeyTimeCode);
      return HOUR_MILLI_SECONDS - timeEllapsed < 0;
    }
    return true;
  }

  validateVisibilityKey() {
    if (this.itineraryDetails.visibilityKey) {
      if (this.visibilityKeyId === this.itineraryDetails.visibilityKey) {
        return true;
      } else {
        return false;
      }
    }
  }

  toggleLikeItinerary() {
    if (!this.isLikeRequestPending) {
      this.loadingService.enableLoadingMask();
      this.isLikeRequestPending = true;
      const isLiked = this.itineraryLikes.includes(this.userEmail);
      if (isLiked) {
        this.subscriptions.add(
          this.itineraryService
            .unlikeItinerary(this.itineraryDetails.id, this.userEmail)
            .subscribe((likeItiData) => {
              console.log("Successfully unliked the itinerary:", likeItiData);
              this.itineraryService
                .getItineraryLikes(this.itineraryDetails.id)
                .subscribe((likeItiUpdateData: any) => {
                  console.log("Itinerary likes:", likeItiUpdateData);
                  this.loadingService.disableLoadingMask();
                  this.itineraryLikes = likeItiUpdateData.listOfUsers;
                });
            })
        );
      } else {
        this.subscriptions.add(
          this.itineraryService
            .likeItinerary(this.itineraryDetails.id, this.userEmail)
            .subscribe((likeItiData) => {
              console.log("Successfully liked the itinerary:", likeItiData);
              this.itineraryService
                .getItineraryLikes(this.itineraryDetails.id)
                .subscribe((likeItiUpdateData: any) => {
                  console.log("Itinerary likes:", likeItiUpdateData);
                  this.loadingService.disableLoadingMask();
                  this.itineraryLikes = likeItiUpdateData.listOfUsers;
                });
            })
        );
      }
    }
    this.isLikeRequestPending = false;
  }

  getNewKey(): string {
    console.log("generating new key");
    const newVisibilityKey = `p${new Date().getTime().toString()}x`;
    console.log("newly generated key:", newVisibilityKey);
    return newVisibilityKey;
  }

  updateItinerary(): void {
    if (this.isVisibilityKeyExpired(this.itineraryDetails.visibilityKey)) {
      const itinerary = cloneDeep(this.itineraryDetails);
      // the _id property needs to be deleted in order for Mongo to work as expected
      itinerary.destinations.forEach((dest) => {
        delete dest["_id"];
        dest["_id"] = dest["id"];
        delete dest["id"];
      });
      itinerary.visibilityKey = this.getNewKey();
      this.loadingService.enableLoadingMask();
      this.itineraryService.updateItinerary(itinerary.id, itinerary).subscribe(
        (data) => {
          console.log("itinerary has been successfully updated:", data);
          this.itineraryDetails.visibilityKey = data.visibilityKey;
          this.loadingService.disableLoadingMask();
          this.alertsService.success("New Visibility Key has been generated!");
        },
        (error) => {
          console.log("An error occured:", error);
          this.loadingService.disableLoadingMask();
          this.alertsService.error(
            "Sorry, an error occurred. Please try again."
          );
        }
      );
    }
  }

  getTripBudget(): string {
    let result = "0";
    if (this.userInfo.adminUser) {
      this.itineraryDetails.destinations.forEach((destination) => {
        result = (
          parseInt(this.itineraryDetails.budgetId) +
          parseInt(destination.budget)
        ).toString();
      });
    } else {
      result = this.itineraryDetails.budgetId;
    }
    return result;
  }

  ngOnDestroy() {
    this.subscriptions.unsubscribe();
  }
}

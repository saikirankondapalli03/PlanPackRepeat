import { Component, OnInit, AfterViewInit, OnDestroy } from '@angular/core';
import { AuthService } from 'src/app/auth/auth.service';
import { UserService } from '../../services/user.service';
import { LoadingService } from '../../shared/loading/loading.service';
import { ToastrService } from 'ngx-toastr';
import { User } from 'src/app/models/new-user';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit, AfterViewInit, OnDestroy {

  userInfo: User;
  pageMode = PageMode;
  pageType = PageMode.EDIT;
  isNewUser: boolean;
  subscription = new Subscription();

  profileForm = {
    fName: '',
    lName: '',
    email: '',
    phone: '',
    bio: '',
    adminUser: false
  }

  constructor(public auth: AuthService, private loadingService: LoadingService, private alerts: ToastrService, private userService: UserService) { }

  ngOnInit() {
    this.loadingService.enableLoadingMask();
    this.subscription.add(this.auth.userProfile$.subscribe(
      profile => {
        if (profile) {
          this.profileForm.email = profile.email;
          this.initUserProfile();
        }
  }));
}

ngAfterViewInit() {
  /**
   * this should only be triggered if the user is a new user
   */
  if (!this.auth.checkIfTheUserIsExisting) {
    this.pageType = this.pageMode.EDIT;
  } else {
    this.pageType = this.pageMode.READ;
  }
}

initUserProfile(): void {
  this.subscription.add(this.userService.getUserInfo(this.profileForm.email).subscribe((
    data: User
  ) => {
    console.log('this is the user info:', data);
    this.isNewUser = false;
    this.userInfo = data;
    this.pageType = this.pageMode.READ;
    this.loadingService.disableLoadingMask();
  },
  (error) => {
    console.log('Error:', error);
    if (error.status === 404) {
      this.isNewUser = true;
      this.alerts.info("Welcome! To continue, please complete your profile.");
      this.pageType = this.pageMode.EDIT;
    } else {
      this.alerts.error('Error: Please try again by refreshing your browser.');
    }
    this.loadingService.disableLoadingMask();
  }));
}

editProfile(): void {
  this.pageType = this.pageMode.EDIT;
  this.profileForm = {
    fName: this.userInfo.firstName,
    lName: this.userInfo.lastName,
    email: this.userInfo.email,
    phone: this.userInfo.mobileNumber,
    bio: this.userInfo.biography,
    adminUser: this.userInfo.adminUser
  }
}

saveProfile(): void {
  this.alerts.success("Your changes are saved!")
  console.log('Saved the profile.');
}

completeProfileSetup(isSuperUser: boolean): void {
  if (this.auth.checkIfTheUserIsExisting || this.isNewUser) {
    this.profileForm.adminUser = isSuperUser;
    console.log(this.profileForm);
    this.createNewUser();
  } else {
    this.alerts.success('Changes successfully saved!');
  }
}

createNewUser() {
  const newUser: User =  {
    firstName: this.profileForm.fName,
    lastName: this.profileForm.lName,
    email: this.profileForm.email,
    mobileNumber: this.profileForm.phone,
    biography: this.profileForm.bio,
    favtDest: "",
    smdetails: [],
    profileImageUrl: "",
    adminUser: this.profileForm.adminUser
  };
  this.subscription.add(this.userService.createUser(newUser).subscribe(
    (newUserData: User) => {
      this.loadingService.disableLoadingMask();
      this.alerts.success('Changes successfully saved!');
      this.pageType = this.pageMode.READ;
      this.userInfo = newUserData;
    },
    (error) => {
      this.alerts.error('An error occurred! Try saving again.');
      console.error('An error occurred while creating the new user:', error);
      this.loadingService.disableLoadingMask();
    }
  ));
}

saveUserProfileChanges(): void {
  this.loadingService.enableLoadingMask();
  const userObj: User =  {
    firstName: this.profileForm.fName,
    lastName: this.profileForm.lName,
    email: this.profileForm.email,
    mobileNumber: this.profileForm.phone,
    biography: this.profileForm.bio,
    favtDest: "",
    smdetails: [],
    profileImageUrl: "",
    adminUser: this.profileForm.adminUser,
    id: this.userInfo.id
  };
  this.subscription.add(
    this.userService.updateUser(userObj).subscribe(
      (data: User) => {
        console.log('Updated user object:', data);
        this.userInfo = data;
        this.pageType = this.pageMode.READ;
        this.loadingService.disableLoadingMask();
        this.alerts.success('Changes successfully saved!');
      },
      (error) => {
        console.log('Error: couldnt update user info', error);
        this.alerts.error('An error occurred! Try saving again.');
        this.pageType = this.pageMode.READ;
        this.loadingService.disableLoadingMask();
      }
    ));
}

saveUserProfile(event) {
  const newUser: User =  {
    firstName: this.profileForm.fName,
    lastName: this.profileForm.lName,
    email: this.profileForm.email,
    mobileNumber: this.profileForm.phone,
    biography: this.profileForm.bio,
    adminUser: this.profileForm.adminUser
  };
  this.subscription.add(this.userService.updateUser(newUser).subscribe(
    (newUserData: User) => {
      this.loadingService.disableLoadingMask();
      this.alerts.success('Changes successfully saved!');
    },
    (error) => {
      this.alerts.error('An error occurred! Try saving again.');
      console.error('An error occurred while creating the new user:', error);
      this.loadingService.disableLoadingMask();
    }
  ));
}

onClickSave(): void {
  if (this.isNewUser) {
    this.completeProfileSetup(true);
  } else {
    this.saveUserProfileChanges();
  }
}

onClickCancel(): void {
  if (this.isNewUser) {
    this.completeProfileSetup(false);
  } else {
    this.pageType = this.pageMode.READ;
  }
}

// resetForm(): void {
//   this.profileForm = {
//     fName: this.userInfo.firstName,
//     lName: this.userInfo.lastName,
//     email: this.userInfo.email,
//     phone: this.userInfo.mobileNumber,
//     bio: this.userInfo.biography,
//     adminUser: this.userInfo.adminUser,
//   }
// }

isFormValid(): boolean {
  return (this.profileForm.fName.trim() !== '' &&
          this.profileForm.lName.trim() !== '' &&
          this.profileForm.bio.trim() !== '' &&
          this.profileForm.phone.trim() !== '')
}

ngOnDestroy() {
  this.subscription.unsubscribe();
}
}

enum PageMode {
  EDIT, READ
}

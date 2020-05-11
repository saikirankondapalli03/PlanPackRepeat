import { Component, OnInit, OnDestroy } from '@angular/core';
import { AuthService } from 'src/app/auth/auth.service';
import { UserService } from '../../services/user.service';
import { ToastrService } from 'ngx-toastr';
import { LoadingService } from '../../shared/loading/loading.service';
import { Subscription } from 'rxjs';
import { Router } from '@angular/router';

@Component({
  selector: 'app-hero',
  templateUrl: './hero.component.html',
  styleUrls: ['./hero.component.css']
})
export class HeroComponent implements OnInit, OnDestroy {

  userProfileJson: any;
  componentSubscription = new Subscription();

  constructor(private authService: AuthService, private userService: UserService,
    private alerts: ToastrService, private loadingService: LoadingService, private router: Router) { }

  ngOnInit() {
    if (this.authService.checkIfTheUserIsExisting) {
      this.componentSubscription.add(this.authService.userProfile$.subscribe(
        profile => {
          this.userProfileJson = JSON.stringify(profile, null, 2);
          if (typeof profile !== 'undefined') {
            this.componentSubscription.add(this.userService.getUserInfo(profile.email).subscribe(
              (userData) => {
                this.alerts.success('Welcome to Plan Pack Repeat!');
                this.userService.userEmail = userData.email;
                this.userService.isSuperUser = userData.adminUser;
                this.loadingService.disableLoadingMask();
              }, (error) => {
                if (error.status === 404) {
                  console.error('An error occurred while creating the new user:', error);
                  this.userService.userEmail = profile.email;
                  this.router.navigateByUrl('/profile')
                }
              }
            ));
          }
        }
        ));
    }
  }

  ngOnDestroy() {
    this.componentSubscription.unsubscribe();
  }

}

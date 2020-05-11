import { Component, OnInit } from '@angular/core';
import { UrlTree, Router } from '@angular/router';

import { AuthService } from '../../auth/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  constructor(private router: Router, private authService: AuthService) {
    this.authService.isAuthenticated$.subscribe(
      (loggedIn => {
        console.log('****', UrlTree, window.location.href);
        if (!loggedIn) {
            console.log('Need to stay here.');
        } else {
            this.router.navigateByUrl('/dashboard');
            return false;
        }
      })
    );
  }

  ngOnInit() {
  }

  login(): void {
    this.authService.login('/dashboard');
  }

}

import { Component, OnInit } from '@angular/core';
import { AuthService } from 'src/app/auth/auth.service';
import { LoadingService } from '../../shared/loading/loading.service';

@Component({
  selector: 'app-callback',
  templateUrl: './callback.component.html',
  styleUrls: ['./callback.component.css']
})
export class CallbackComponent implements OnInit {

  constructor(private auth: AuthService, private loadingService: LoadingService) { }

  ngOnInit() {
    this.loadingService.enableLoadingMask();
    setTimeout(() => {
      this.auth.handleAuthCallback();
    }, 1000);
  }

}

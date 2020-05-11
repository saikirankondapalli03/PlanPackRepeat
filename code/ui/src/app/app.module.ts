import { MessageService } from './services/message.service';
import { PagerService } from './services/pager.service';
import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule, FormGroup } from '@angular/forms';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { HighlightModule } from 'ngx-highlightjs';
import json from 'highlight.js/lib/languages/json';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { SharedModule } from './shared/shared.module';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { CallbackComponent } from './pages/callback/callback.component';
import { HomeComponent } from './pages/home/home.component';
import { ProfileComponent } from './pages/profile/profile.component';
import { NavBarComponent } from './components/nav-bar/nav-bar.component';
import { FooterComponent } from './components/footer/footer.component';
import { HeroComponent } from './components/hero/hero.component';
import { HomeContentComponent } from './components/home-content/home-content.component';
import { LoginComponent } from './components/login/login.component';

import { HttpClientModule, HttpClient } from '@angular/common/http';

import { UserService } from './services/user.service';
import { LoadingService } from './shared/loading/loading.service'
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ToastrModule } from 'ngx-toastr';
import { CreateItineraryComponent } from './pages/create-itinerary/create-itinerary.component';
import { AgmCoreModule } from '@agm/core';
import { ItineraryModule } from './itinerary/itinerary.module';
import { HomePageHeaderComponent } from './components/home-page-header/home-page-header.component';
import { HomePageCarouselViewComponent } from './components/home-page-carousel-view/home-page-carousel-view.component';
import { SearchItineraryComponent } from './components/search-itinerary/search-itinerary.component';

export function hljsLanguages() {
  return [{ name: 'json', func: json }];
}

@NgModule({
  declarations: [
    AppComponent,
    CallbackComponent,
    HomeComponent,
    ProfileComponent,
    NavBarComponent,
    FooterComponent,
    HeroComponent,
    HomeContentComponent,
    LoginComponent,
    CreateItineraryComponent,
    HomePageHeaderComponent,
    HomePageCarouselViewComponent,
    SearchItineraryComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    NgbModule,
    FormsModule,
    HighlightModule.forRoot({
      languages: hljsLanguages
    }),
    AgmCoreModule.forRoot({
      apiKey: 'AIzaSyAzdiZyPOCqTWmA8g-WVGYFG5DuDAIsV4I',
      libraries: ['places']
    }),
    FontAwesomeModule,
    HttpClientModule,
    BrowserAnimationsModule,
    ToastrModule.forRoot({
      timeOut: 4000,
      positionClass: 'toast-top-right'
    }),
    ItineraryModule,
    SharedModule
  ],
  providers: [ UserService, HttpClient, LoadingService, PagerService, MessageService ],
  bootstrap: [AppComponent]
})
export class AppModule { }

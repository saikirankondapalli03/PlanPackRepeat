import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

// import { CreateItineraryComponent } from '../pages/create-itinerary/create-itinerary.component';
import { InfoComponent } from './info/info.component';
import { DistinationsComponent } from './distinations/distinations.component';
import { ProgressTrackerComponent } from './progress-tracker/progress-tracker.component';
import { SummaryComponent } from './summary/summary.component';
import { ItineraryBaseComponent } from './itinerary-base/itinerary-base.component';
import { BudgetComponent } from './budget/budget.component';
import { DetailsComponent } from './details/details.component';

const createItineraryRoutes: Routes = [

  { path: ':id', component: DetailsComponent},
  { path: '', component: ItineraryBaseComponent,
  children: [
    { path: '', component: ProgressTrackerComponent, outlet: 'progress tracker' },
    { path: '', component: SummaryComponent, outlet: 'summary'},
    { path: 'create-itinerary/info', component: InfoComponent },
    { path: 'create-itinerary/destinations', component: DistinationsComponent },
    { path: 'create-itinerary/budget', component: BudgetComponent },
    { path: 'edit-itinerary/info', component: InfoComponent },
    { path: 'edit-itinerary/destinations', component: DistinationsComponent },
    { path: 'edit-itinerary/budget', component: BudgetComponent },
  ]
},
{ path: ':id/:visibilityKey', component: DetailsComponent },
];

@NgModule({
  imports: [ RouterModule.forChild(createItineraryRoutes) ],
  exports: [ RouterModule ]
})

export class ItineraryRoutesModule {}
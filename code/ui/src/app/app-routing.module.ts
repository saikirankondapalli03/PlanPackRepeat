import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { HomeComponent } from './pages/home/home.component';
import { CallbackComponent } from './pages/callback/callback.component';
import { AuthGuard } from './auth/auth.guard';
import { ProfileCompletionGuard } from './route-guard/profile-completion.guard';
import { ProfileComponent } from './pages/profile/profile.component';
import { LoginComponent } from './components/login/login.component';
import { PaginationGuard } from './pagination/pagination.guard';
import { AzureAdGuard } from './auth/azure-ad/azure-ad.guard';
import { CallbackAzureComponent } from './pages/callback-azure/callback-azure.component';

const routes: Routes = [
  {
    path: 'callback',
    component: CallbackComponent
  },
  {
    path: 'callback-azure',
    component: CallbackAzureComponent
  },
  {
    path: 'profile',
    component: ProfileComponent,
    // canActivate: [AuthGuard]
  },
  {
    path: 'dashboard',
    component: HomeComponent,
    canActivate: [ProfileCompletionGuard]
  },
  { path: 'itinerary',
    loadChildren: () => import('./itinerary/itinerary.module').then(m => m.ItineraryModule),
    canActivate: [ProfileCompletionGuard]
  },
  {
    path: 'login',
    component: LoginComponent
  },
  {
    path: 'pagination',
    loadChildren: () =>
      import('./pagination/pagination.module').then(m => m.PaginationModule),
    canActivate: [PaginationGuard]
  },
  {
    path: 'pagination-azure',
    loadChildren: () =>
      import('./pagination/pagination.module').then(m => m.PaginationModule),
    canActivate: [AzureAdGuard]
  },
  {
    path: '',
    redirectTo: '/login',
    pathMatch: 'full'
  },
  { path: 'create-itinerary',
    loadChildren: () => import('./itinerary/itinerary.module').then(m => m.ItineraryModule),
    canActivate: [ProfileCompletionGuard]
  },

  {
    path: '**',
    redirectTo: '/login',
    pathMatch: 'full'
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }

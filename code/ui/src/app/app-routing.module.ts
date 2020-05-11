import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { HomeComponent } from './pages/home/home.component';
import { CallbackComponent } from './pages/callback/callback.component';
import { AuthGuard } from './auth/auth.guard';
import { ProfileCompletionGuard } from './route-guard/profile-completion.guard';
import { ProfileComponent } from './pages/profile/profile.component';
import { LoginComponent } from './components/login/login.component';

const routes: Routes = [
  {
    path: 'callback',
    component: CallbackComponent
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

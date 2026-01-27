import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { PaginationComponent } from './pagination.component';
import { PaginationGuard } from './pagination.guard';

/**
 * Interview: Part 1 & 6 - Routing
 * - Route config: path -> component
 * - canActivate: Route Guard (Part 6)
 */
const routes: Routes = [
  {
    path: '',
    component: PaginationComponent,
    canActivate: [PaginationGuard]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class PaginationRoutingModule {}

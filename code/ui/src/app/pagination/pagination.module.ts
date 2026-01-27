import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { PaginationRoutingModule } from './pagination-routing.module';
import { PaginationComponent } from './pagination.component';
import { StatusBadgePipe } from './pipes/status-badge.pipe';

/**
 * Interview: Part 1 - Modules
 * - declarations: components, pipes that belong to this module
 * - imports: CommonModule (ngIf, ngFor, etc.), Forms, Router
 * - Part 5: ReactiveFormsModule, FormsModule for form controls
 */
@NgModule({
  declarations: [PaginationComponent, StatusBadgePipe],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    PaginationRoutingModule
  ]
})
export class PaginationModule {}

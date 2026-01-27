import {
  Component,
  OnInit,
  OnDestroy,
  ViewChild,
  ElementRef,
  AfterViewInit,
  ChangeDetectionStrategy,
  ChangeDetectorRef
} from '@angular/core';
import { FormControl, Validators } from '@angular/forms';
import { Subscription } from 'rxjs';
import { PaginationService } from './pagination.service';
import { PaginationItem } from './models/pagination-item.interface';

/**
 * Interview concepts covered:
 * - Part 1: Component, DI (constructor inject PaginationService), Module
 * - Part 2: Interpolation, property/event binding, *ngFor, *ngIf, [ngClass], trackBy
 * - Part 5: Reactive Forms (FormControl for page size)
 * - Part 7: ngOnInit, ngOnDestroy (unsubscribe)
 * - Part 8: ViewChild, ChangeDetectionStrategy.OnPush, custom pipe, ChangeDetectorRef
 * - Part 4: Observable subscribe, async pipe alternative (manual sub for demo)
 */
@Component({
  selector: 'app-pagination',
  templateUrl: './pagination.component.html',
  styleUrls: ['./pagination.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PaginationComponent implements OnInit, OnDestroy, AfterViewInit {
  /** Part 8 - ViewChild: access DOM element (table container) for scroll/focus */
  @ViewChild('tableContainer', { static: false }) tableContainer: ElementRef<HTMLDivElement>;

  /** Part 2 - Data for template: interpolation, *ngFor */
  pagedData: { items: PaginationItem[]; totalItems: number; totalPages: number; pages: number[] };
  currentPage = 1;
  pageSize = 4;

  /** Part 5 - Reactive Forms: FormControl for page size with validators */
  pageSizeControl = new FormControl(4, [
    Validators.required,
    Validators.min(1),
    Validators.max(20)
  ]);

  /** Part 4 - Manual subscription; we unsubscribe in ngOnDestroy */
  private subscription = new Subscription();
  lastApplied: PaginationItem | null = null;

  constructor(
    private paginationService: PaginationService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadPage();
    this.subscription.add(
      this.pageSizeControl.valueChanges.subscribe((val: number) => {
        if (this.pageSizeControl.valid && val >= 1 && val <= 20) {
          this.pageSize = val;
          this.currentPage = 1;
          this.loadPage();
          this.cdr.markForCheck();
        }
      })
    );
    this.subscription.add(
      this.paginationService.lastApplied$.subscribe((item) => {
        this.lastApplied = item;
        this.cdr.markForCheck();
      })
    );
    this.cdr.markForCheck();
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  ngAfterViewInit(): void {
    // ViewChild available here; could scroll to top, focus, etc.
  }

  loadPage(): void {
    this.pagedData = this.paginationService.getPagedItems(
      this.currentPage,
      this.pageSize
    );
  }

  setPage(page: number): void {
    if (!this.pagedData || page < 1 || page > this.pagedData.totalPages) return;
    this.currentPage = page;
    this.loadPage();
    this.scrollToTable();
    this.cdr.markForCheck();
  }

  /** Part 2 - Event binding: (click)="applyItem(row)" */
  applyItem(item: PaginationItem): void {
    this.paginationService.applyItem(item);
  }

  /** Part 2 - trackBy for *ngFor performance */
  trackById(_index: number, item: PaginationItem): number {
    return item.id;
  }

  private scrollToTable(): void {
    if (this.tableContainer && this.tableContainer.nativeElement) {
      this.tableContainer.nativeElement.scrollIntoView({ behavior: 'smooth' });
    }
  }
}

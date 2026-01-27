import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { PaginationItem } from './models/pagination-item.interface';

/**
 * Interview: Part 3 - Services & DI
 * - @Injectable({ providedIn: 'root' }) = singleton, app-wide, tree-shakeable
 * - Service holds business logic and shared data
 * - Part 4 - Observables: BehaviorSubject for "last applied" row, components subscribe
 */
@Injectable({
  providedIn: 'root'
})
export class PaginationService {

  /** Static data - 4 columns: id, name, category, status */
  private readonly items: PaginationItem[] = [
    { id: 1, name: 'Item Alpha', category: 'Electronics', status: 'Active' },
    { id: 2, name: 'Item Beta', category: 'Clothing', status: 'Pending' },
    { id: 3, name: 'Item Gamma', category: 'Books', status: 'Inactive' },
    { id: 4, name: 'Item Delta', category: 'Electronics', status: 'Active' },
    { id: 5, name: 'Item Epsilon', category: 'Home', status: 'Pending' },
    { id: 6, name: 'Item Zeta', category: 'Sports', status: 'Active' },
    { id: 7, name: 'Item Eta', category: 'Clothing', status: 'Inactive' },
    { id: 8, name: 'Item Theta', category: 'Books', status: 'Pending' },
    { id: 9, name: 'Item Iota', category: 'Electronics', status: 'Active' },
    { id: 10, name: 'Item Kappa', category: 'Home', status: 'Inactive' },
    { id: 11, name: 'Item Lambda', category: 'Sports', status: 'Active' },
    { id: 12, name: 'Item Mu', category: 'Clothing', status: 'Pending' },
    { id: 13, name: 'Item Nu', category: 'Books', status: 'Active' },
    { id: 14, name: 'Item Xi', category: 'Electronics', status: 'Inactive' },
    { id: 15, name: 'Item Omicron', category: 'Home', status: 'Pending' }
  ];

  /** Part 4 - BehaviorSubject: has initial value, new subscribers get current value */
  private lastAppliedSubject = new BehaviorSubject<PaginationItem | null>(null);
  lastApplied$: Observable<PaginationItem | null> = this.lastAppliedSubject.asObservable();

  constructor() {}

  getAllItems(): PaginationItem[] {
    return this.items;
  }

  applyItem(item: PaginationItem): void {
    this.lastAppliedSubject.next(item);
  }

  /**
   * Pager logic: return slice of items for current page.
   * Interview: reusable logic in service, not in component.
   */
  getPagedItems(currentPage: number, pageSize: number): {
    items: PaginationItem[];
    totalItems: number;
    totalPages: number;
    startIndex: number;
    endIndex: number;
    pages: number[];
  } {
    const totalItems = this.items.length;
    const totalPages = Math.ceil(totalItems / pageSize);
    const safePage = Math.max(1, Math.min(currentPage, totalPages));
    const startIndex = (safePage - 1) * pageSize;
    const endIndex = Math.min(startIndex + pageSize, totalItems);
    const items = this.items.slice(startIndex, endIndex);

    const maxPages = 5;
    let startPage = Math.max(1, safePage - Math.floor(maxPages / 2));
    const endPage = Math.min(totalPages, startPage + maxPages - 1);
    if (endPage - startPage + 1 < maxPages) {
      startPage = Math.max(1, endPage - maxPages + 1);
    }
    const pages = Array.from({ length: endPage - startPage + 1 }, (_, i) => startPage + i);

    return {
      items,
      totalItems,
      totalPages,
      startIndex,
      endIndex,
      pages
    };
  }
}

/**
 * Model for a single row in the pagination table.
 * Interview: Part 1 - Project structure (models folder), TypeScript interfaces.
 */
export interface PaginationItem {
  id: number;
  name: string;
  category: string;
  status: 'Active' | 'Pending' | 'Inactive';
}

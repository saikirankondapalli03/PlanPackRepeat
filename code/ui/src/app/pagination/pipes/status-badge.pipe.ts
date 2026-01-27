import { Pipe, PipeTransform } from '@angular/core';

/**
 * Interview: Part 8 - Custom Pipes
 * Transforms status string into a CSS class name for styling badges.
 * Usage: {{ item.status | statusBadge }}
 */
@Pipe({
  name: 'statusBadge'
})
export class StatusBadgePipe implements PipeTransform {
  transform(status: string): string {
    if (!status) return 'badge-secondary';
    switch (status) {
      case 'Active': return 'badge-success';
      case 'Pending': return 'badge-warning';
      case 'Inactive': return 'badge-secondary';
      default: return 'badge-light';
    }
  }
}

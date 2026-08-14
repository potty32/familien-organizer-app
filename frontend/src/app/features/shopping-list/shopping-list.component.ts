import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ShoppingListService } from '../../core/services/shopping-list.service';
import { SessionService } from '../../core/services/session.service';
import { ShoppingItem } from '../../core/models/shopping-item.model';

@Component({
  selector: 'app-shopping-list',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './shopping-list.component.html',
  styleUrl: './shopping-list.component.scss'
})
export class ShoppingListComponent implements OnInit {
  private shoppingService = inject(ShoppingListService);
  private session = inject(SessionService);
  private fb = inject(FormBuilder);

  items = signal<ShoppingItem[]>([]);
  loading = signal(false);
  submitting = signal(false);

  pendingItems = computed(() => this.items().filter(i => i.status === 'PENDING'));
  boughtItems = computed(() => this.items().filter(i => i.status === 'BOUGHT'));
  rejectedItems = computed(() => this.items().filter(i => i.status === 'REJECTED'));
  isParent = computed(() => this.session.activeUser()?.role === 'PARENT');

  form = this.fb.group({
    name: ['', [Validators.required, Validators.maxLength(100)]],
    note: ['']
  });

  ngOnInit(): void {
    this.load();
  }

  private load(): void {
    this.loading.set(true);
    this.shoppingService.getAll().subscribe({
      next: items => { this.items.set(items); this.loading.set(false); },
      error: () => this.loading.set(false)
    });
  }

  submit(): void {
    if (this.form.invalid || this.submitting()) return;
    this.submitting.set(true);
    const { name, note } = this.form.getRawValue();
    this.shoppingService.create({ name: name!, note: note || undefined }).subscribe({
      next: item => {
        this.items.update(list => [item, ...list]);
        this.form.reset();
        this.submitting.set(false);
      },
      error: () => this.submitting.set(false)
    });
  }

  buy(item: ShoppingItem): void {
    this.shoppingService.buy(item.id).subscribe({
      next: updated => this.replaceItem(updated)
    });
  }

  reject(item: ShoppingItem): void {
    this.shoppingService.reject(item.id).subscribe({
      next: updated => this.replaceItem(updated)
    });
  }

  delete(item: ShoppingItem): void {
    this.shoppingService.delete(item.id).subscribe({
      next: () => this.items.update(list => list.filter(i => i.id !== item.id))
    });
  }

  canDelete(item: ShoppingItem): boolean {
    const user = this.session.activeUser();
    if (!user) return false;
    if (user.role === 'PARENT') return true;
    return item.addedBy.id === user.id && !item.pointsProcessed;
  }

  private replaceItem(updated: ShoppingItem): void {
    this.items.update(list => list.map(i => i.id === updated.id ? updated : i));
  }
}

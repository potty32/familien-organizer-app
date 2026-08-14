import { TaskUser } from './task.model';

export type ShoppingItemStatus = 'PENDING' | 'BOUGHT' | 'REJECTED';

export interface ShoppingItem {
  id: string;
  name: string;
  note?: string;
  status: ShoppingItemStatus;
  addedBy: TaskUser;
  pointsProcessed: boolean;
  createdAt: string;
}

export interface CreateShoppingItemRequest {
  name: string;
  note?: string;
}

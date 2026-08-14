import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ShoppingItem, CreateShoppingItemRequest } from '../models/shopping-item.model';

@Injectable({ providedIn: 'root' })
export class ShoppingListService {
  private http = inject(HttpClient);
  private base = `${environment.apiBaseUrl}/shopping-items`;

  getAll(): Observable<ShoppingItem[]> {
    return this.http.get<ShoppingItem[]>(this.base);
  }

  create(request: CreateShoppingItemRequest): Observable<ShoppingItem> {
    return this.http.post<ShoppingItem>(this.base, request);
  }

  buy(id: string): Observable<ShoppingItem> {
    return this.http.patch<ShoppingItem>(`${this.base}/${id}/buy`, {});
  }

  reject(id: string): Observable<ShoppingItem> {
    return this.http.patch<ShoppingItem>(`${this.base}/${id}/reject`, {});
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }
}

import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { MealWish, CreateMealWishRequest, AcceptMealWishRequest } from '../models/meal-wish.model';

@Injectable({ providedIn: 'root' })
export class MealWishService {
  private readonly http = inject(HttpClient);
  private readonly base = `${environment.apiBaseUrl}/meal-wishes`;

  getAll(): Observable<MealWish[]> {
    return this.http.get<MealWish[]>(this.base);
  }

  getWeeklyPlan(): Observable<MealWish[]> {
    return this.http.get<MealWish[]>(`${this.base}/weekly-plan`);
  }

  create(request: CreateMealWishRequest): Observable<MealWish> {
    return this.http.post<MealWish>(this.base, request);
  }

  accept(id: string, request: AcceptMealWishRequest): Observable<MealWish> {
    return this.http.patch<MealWish>(`${this.base}/${id}/accept`, request);
  }

  reject(id: string): Observable<MealWish> {
    return this.http.patch<MealWish>(`${this.base}/${id}/reject`, {});
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }
}

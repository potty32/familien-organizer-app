import { TaskUser } from './task.model';

export type MealWishStatus = 'PENDING' | 'ACCEPTED' | 'REJECTED';

export interface MealWish {
  id: string;
  name: string;
  description?: string;
  status: MealWishStatus;
  suggestedBy: TaskUser;
  weeklyPlanDate?: string;
  pointsAwarded: boolean;
  createdAt: string;
}

export interface CreateMealWishRequest {
  name: string;
  description?: string;
}

export interface AcceptMealWishRequest {
  weeklyPlanDate: string;
}

export interface WeekDay {
  date: string;
  label: string;
  shortLabel: string;
  isToday: boolean;
  meals: MealWish[];
}

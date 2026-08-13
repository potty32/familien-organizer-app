export type UserRole = 'PARENT' | 'CHILD';

export interface User {
  id: string;
  displayName: string;
  avatarColor: string;
  role: UserRole;
  totalPoints: number;
}

export interface CreateUserRequest {
  displayName: string;
  avatarColor: string;
  role: UserRole;
  pinCode?: string;
}

export interface SessionRequest {
  userId: string;
  pinCode?: string;
}

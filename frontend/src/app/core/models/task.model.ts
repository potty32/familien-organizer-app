export type TaskStatus = 'OPEN' | 'IN_PROGRESS' | 'DONE';
export type RecurrencePattern = 'DAILY' | 'WEEKLY' | 'MONTHLY';

export interface TaskUser {
  id: string;
  displayName: string;
  avatarColor: string;
}

export interface Task {
  id: string;
  title: string;
  description?: string;
  status: TaskStatus;
  points?: number;
  assignedTo: TaskUser;
  createdBy: TaskUser;
  dueDate?: string;
  isRecurring: boolean;
  recurrencePattern?: RecurrencePattern;
  createdAt: string;
}

export interface CreateTaskRequest {
  title: string;
  description?: string;
  points?: number;
  assignedToId: string;
  dueDate?: string;
  isRecurring: boolean;
  recurrencePattern?: RecurrencePattern;
}

export interface UpdateTaskStatusRequest {
  status: TaskStatus;
}

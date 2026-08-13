import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Task, CreateTaskRequest, TaskStatus, UpdateTaskStatusRequest } from '../models/task.model';

@Injectable({ providedIn: 'root' })
export class TaskService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiBaseUrl}/tasks`;

  getAll(filters?: { status?: TaskStatus; assignedToId?: string }): Observable<Task[]> {
    let params = new HttpParams();
    if (filters?.status) params = params.set('status', filters.status);
    if (filters?.assignedToId) params = params.set('assignedTo', filters.assignedToId);
    return this.http.get<Task[]>(this.baseUrl, { params });
  }

  getMyTasks(): Observable<Task[]> {
    return this.http.get<Task[]>(`${this.baseUrl}/mine`);
  }

  getById(id: string): Observable<Task> {
    return this.http.get<Task>(`${this.baseUrl}/${id}`);
  }

  create(request: CreateTaskRequest): Observable<Task> {
    return this.http.post<Task>(this.baseUrl, request);
  }

  update(id: string, request: Partial<CreateTaskRequest>): Observable<Task> {
    return this.http.put<Task>(`${this.baseUrl}/${id}`, request);
  }

  updateStatus(id: string, status: TaskStatus): Observable<Task> {
    const body: UpdateTaskStatusRequest = { status };
    return this.http.patch<Task>(`${this.baseUrl}/${id}/status`, body);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}

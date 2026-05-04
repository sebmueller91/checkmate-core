import { api } from './client'
import type { AuthResponse, UserResponse, GameSummary } from '@/types/protocol'

export const authApi = {
  register: (email: string, password: string) =>
    api.post<AuthResponse>('/auth/register', { email, password }),

  login: (email: string, password: string) =>
    api.post<AuthResponse>('/auth/login', { email, password }),

  refresh: () =>
    api.post<AuthResponse>('/auth/refresh'),

  me: () =>
    api.get<UserResponse>('/me'),

  games: (userId: string) =>
    api.get<GameSummary[]>(`/users/${userId}/games`),
}

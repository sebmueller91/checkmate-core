import { create } from 'zustand'
import { persist } from 'zustand/middleware'

interface AuthState {
  token: string | null
  userId: string | null
  email: string | null
  setAuth: (token: string, userId: string, email: string) => void
  clearAuth: () => void
  isAuthenticated: () => boolean
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set, get) => ({
      token: null,
      userId: null,
      email: null,
      setAuth: (token, userId, email) => set({ token, userId, email }),
      clearAuth: () => set({ token: null, userId: null, email: null }),
      isAuthenticated: () => get().token !== null,
    }),
    { name: 'checkmate-auth' },
  ),
)

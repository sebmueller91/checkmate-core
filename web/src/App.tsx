import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import RequireAuth from '@/lib/RequireAuth'
import LandingPage  from '@/pages/LandingPage'
import LoginPage    from '@/pages/LoginPage'
import RegisterPage from '@/pages/RegisterPage'
import LobbyPage    from '@/pages/LobbyPage'
import GamePage     from '@/pages/GamePage'
import HistoryPage  from '@/pages/HistoryPage'
import ProfilePage  from '@/pages/ProfilePage'

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* Public */}
        <Route path="/"         element={<LandingPage />} />
        <Route path="/login"    element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />

        {/* Authenticated */}
        <Route path="/lobby"   element={<RequireAuth><LobbyPage /></RequireAuth>} />
        <Route path="/game"    element={<RequireAuth><GamePage /></RequireAuth>} />
        <Route path="/history" element={<RequireAuth><HistoryPage /></RequireAuth>} />
        <Route path="/profile" element={<RequireAuth><ProfilePage /></RequireAuth>} />

        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
  )
}

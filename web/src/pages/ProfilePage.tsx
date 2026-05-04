import { useNavigate } from 'react-router-dom'
import { useAuthStore } from '@/store/auth'

// TODO: replace with designed UI components; add stats summary from game history
export default function ProfilePage() {
  const navigate  = useNavigate()
  const { email, clearAuth } = useAuthStore()

  function signOut() {
    clearAuth()
    navigate('/')
  }

  return (
    <div>
      <p>{email}</p>
      <a href="/history">Game history</a>
      <button onClick={signOut}>Sign out</button>
    </div>
  )
}

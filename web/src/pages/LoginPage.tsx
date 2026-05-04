import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { authApi } from '@/api/auth'
import { useAuthStore } from '@/store/auth'
import { ApiError } from '@/api/client'

// TODO: replace with designed UI components
export default function LoginPage() {
  const navigate = useNavigate()
  const setAuth = useAuthStore(s => s.setAuth)
  const [email, setEmail]       = useState('')
  const [password, setPassword] = useState('')
  const [error, setError]       = useState<string | null>(null)
  const [loading, setLoading]   = useState(false)

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    setError(null)
    setLoading(true)
    try {
      const res = await authApi.login(email, password)
      setAuth(res.token, res.userId, res.email)
      navigate('/lobby')
    } catch (err) {
      setError(err instanceof ApiError && err.status === 401
        ? 'Invalid email or password.'
        : 'Something went wrong. Please try again.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <form onSubmit={handleSubmit}>
      <input type="email"    value={email}    onChange={e => setEmail(e.target.value)}    placeholder="Email"    required />
      <input type="password" value={password} onChange={e => setPassword(e.target.value)} placeholder="Password" required />
      {error && <p role="alert">{error}</p>}
      <button type="submit" disabled={loading}>Log in</button>
      <Link to="/register">Create account</Link>
    </form>
  )
}

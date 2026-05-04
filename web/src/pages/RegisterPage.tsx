import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { authApi } from '@/api/auth'
import { useAuthStore } from '@/store/auth'
import { ApiError } from '@/api/client'

// TODO: replace with designed UI components
export default function RegisterPage() {
  const navigate = useNavigate()
  const setAuth = useAuthStore(s => s.setAuth)
  const [email, setEmail]             = useState('')
  const [password, setPassword]       = useState('')
  const [confirm, setConfirm]         = useState('')
  const [error, setError]             = useState<string | null>(null)
  const [loading, setLoading]         = useState(false)

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    setError(null)
    if (password !== confirm) { setError('Passwords do not match.'); return }
    if (password.length < 8)  { setError('Password must be at least 8 characters.'); return }
    setLoading(true)
    try {
      const res = await authApi.register(email, password)
      setAuth(res.token, res.userId, res.email)
      navigate('/lobby')
    } catch (err) {
      setError(err instanceof ApiError && err.status === 409
        ? 'Email already registered.'
        : 'Something went wrong. Please try again.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <form onSubmit={handleSubmit}>
      <input type="email"    value={email}    onChange={e => setEmail(e.target.value)}    placeholder="Email"            required />
      <input type="password" value={password} onChange={e => setPassword(e.target.value)} placeholder="Password"         required />
      <input type="password" value={confirm}  onChange={e => setConfirm(e.target.value)}  placeholder="Confirm password" required />
      {error && <p role="alert">{error}</p>}
      <button type="submit" disabled={loading}>Create account</button>
      <Link to="/login">Log in</Link>
    </form>
  )
}

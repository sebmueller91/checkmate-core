import { useEffect, useState } from 'react'
import { useAuthStore } from '@/store/auth'
import { authApi } from '@/api/auth'
import type { GameSummary } from '@/types/protocol'

// TODO: replace with designed UI components
export default function HistoryPage() {
  const userId = useAuthStore(s => s.userId)
  const [games, setGames]   = useState<GameSummary[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    if (!userId) return
    authApi.games(userId)
      .then(setGames)
      .finally(() => setLoading(false))
  }, [userId])

  if (loading) return <p>Loading…</p>
  if (games.length === 0) return <p>No games yet.</p>

  return (
    <table>
      <thead>
        <tr><th>Date</th><th>Result</th><th>PGN</th></tr>
      </thead>
      <tbody>
        {games.map(g => (
          <tr key={g.id}>
            <td>{new Date(g.createdAt).toLocaleDateString()}</td>
            <td>{g.result}</td>
            <td>
              <a
                href={`data:text/plain,${encodeURIComponent(g.pgn)}`}
                download={`game-${g.id}.pgn`}
              >
                Download PGN
              </a>
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  )
}

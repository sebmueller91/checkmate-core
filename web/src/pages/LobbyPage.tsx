import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { authApi } from '@/api/auth'
import { useAuthStore } from '@/store/auth'
import { useGameStore } from '@/store/game'
import type { GameSummary, TimeControl } from '@/types/protocol'
import { TIME_CONTROL_PRESETS } from '@/types/protocol'

// TODO: replace with designed UI components
export default function LobbyPage() {
  const navigate  = useNavigate()
  const userId    = useAuthStore(s => s.userId)
  const clearAuth = useAuthStore(s => s.clearAuth)
  const startHumanGame = useGameStore(s => s.startHumanGame)
  const startBotGame   = useGameStore(s => s.startBotGame)

  const [recentGames, setRecentGames] = useState<GameSummary[]>([])
  const [showGameSetup, setShowGameSetup] = useState<'human' | 'bot' | null>(null)
  const [selectedTime, setSelectedTime] = useState<TimeControl | null>(null)

  useEffect(() => {
    if (!userId) return
    authApi.games(userId).then(setRecentGames).catch(() => {})
  }, [userId])

  function createHumanGame() {
    const path = selectedTime
      ? `/game/new?time=${selectedTime.seconds}&increment=${selectedTime.increment}`
      : '/game/new'
    startHumanGame(path)
    navigate('/game')
  }

  function createBotGame(difficulty: string) {
    const path = selectedTime
      ? `/game/bot?difficulty=${difficulty}&side=white&time=${selectedTime.seconds}&increment=${selectedTime.increment}`
      : `/game/bot?difficulty=${difficulty}&side=white`
    startBotGame(path)
    navigate('/game')
  }

  function signOut() {
    clearAuth()
    navigate('/')
  }

  return (
    <div>
      <div>
        <button onClick={() => setShowGameSetup('human')}>Play a friend</button>
        <button onClick={() => setShowGameSetup('bot')}>Play the computer</button>
      </div>

      {showGameSetup === 'human' && (
        <div>
          <h2>New game</h2>
          <TimeControlPicker value={selectedTime} onChange={setSelectedTime} />
          <button onClick={createHumanGame}>Create game</button>
        </div>
      )}

      {showGameSetup === 'bot' && (
        <div>
          <h2>Play vs computer</h2>
          <TimeControlPicker value={selectedTime} onChange={setSelectedTime} />
          {(['beginner', 'easy', 'medium', 'hard', 'expert'] as const).map(d => (
            <button key={d} onClick={() => createBotGame(d)}>{d}</button>
          ))}
        </div>
      )}

      <section>
        <h2>Recent games</h2>
        {recentGames.length === 0 ? (
          <p>No games yet.</p>
        ) : (
          <table>
            <tbody>
              {recentGames.slice(0, 10).map(g => (
                <tr key={g.id}>
                  <td>{g.result}</td>
                  <td>{new Date(g.createdAt).toLocaleDateString()}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
        <a href={`/history`}>Full history</a>
      </section>

      <button onClick={signOut}>Sign out</button>
    </div>
  )
}

function TimeControlPicker({ value, onChange }: { value: TimeControl | null; onChange: (t: TimeControl | null) => void }) {
  return (
    <div>
      <button onClick={() => onChange(null)} aria-pressed={value === null}>No clock</button>
      {TIME_CONTROL_PRESETS.map(tc => (
        <button
          key={tc.label}
          onClick={() => onChange(tc)}
          aria-pressed={value?.label === tc.label}
        >
          {tc.label}
        </button>
      ))}
    </div>
  )
}

import { useCallback } from 'react'
import { useNavigate } from 'react-router-dom'
import { useGameSocket } from '@/hooks/useGameSocket'
import { useGameStore } from '@/store/game'
import type { ServerMessage, ClientMessage, PlayerColor, GameResult, GameEndReason } from '@/types/protocol'

// TODO: replace with designed UI components — board, player cards, move list, clocks
export default function GamePage() {
  const navigate = useNavigate()
  const store = useGameStore()

  const handleMessage = useCallback((msg: ServerMessage) => {
    switch (msg.type) {
      case 'joined':
        store.setJoined(msg.color as PlayerColor, msg.inviteCode)
        break
      case 'state':
        store.applyState(msg)
        break
      case 'draw_offered':
        store.setDrawOffered(msg.by as PlayerColor)
        break
      case 'draw_declined':
        store.setDrawDeclined()
        break
      case 'game_ended':
        store.setGameEnded(msg.result as GameResult, msg.reason as GameEndReason)
        break
      case 'opponent_connected':
        store.setOpponentConnected(true)
        break
      case 'opponent_disconnected':
        store.setOpponentConnected(false)
        break
    }
  }, [store])

  const { send } = useGameSocket(store.wsPath, {
    onMessage: handleMessage,
    onStatusChange: store.setConnectionStatus,
  })

  function sendMove(uci: string) {
    send({ type: 'move', uci })
  }
  function resign()      { send({ type: 'resign' }) }
  function offerDraw()   { send({ type: 'offer_draw' }) }
  function acceptDraw()  { send({ type: 'accept_draw' }) }
  function declineDraw() { send({ type: 'decline_draw' }) }
  function abort()       { send({ type: 'abort' }) }

  if (!store.wsPath) {
    navigate('/lobby')
    return null
  }

  // Waiting for opponent (human game before second player joins)
  if (store.status === 'waiting' && store.inviteCode && !store.inviteCode.startsWith('bot:')) {
    return (
      <div>
        <p>Invite code: <strong>{store.inviteCode}</strong></p>
        <p>Waiting for opponent…</p>
        <button onClick={() => { store.reset(); navigate('/lobby') }}>Cancel</button>
      </div>
    )
  }

  return (
    <div>
      {/* TODO: board component (requires design + chess board library) */}
      <pre>{store.fen ?? 'Loading…'}</pre>

      <div>Turn: {store.turn} | You are: {store.myColor}</div>

      {store.clocks && (
        <div>
          White: {(store.clocks.white / 1000).toFixed(1)}s
          Black: {(store.clocks.black / 1000).toFixed(1)}s
        </div>
      )}

      {store.drawOfferedBy && store.drawOfferedBy !== store.myColor && (
        <div>
          Opponent offers a draw.
          <button onClick={acceptDraw}>Accept</button>
          <button onClick={declineDraw}>Decline</button>
        </div>
      )}

      {store.status === 'ended' && (
        <div>
          <p>Game over: {store.result} ({store.endReason})</p>
          <button onClick={() => { store.reset(); navigate('/lobby') }}>Play again</button>
          <button onClick={() => navigate('/history')}>View history</button>
        </div>
      )}

      {store.status === 'ongoing' && (
        <div>
          <button onClick={resign}>Resign</button>
          <button onClick={offerDraw}>Offer draw</button>
          {/* Abort only available before move 2 — server enforces this, UI shows it always */}
          <button onClick={abort}>Abort</button>
        </div>
      )}

      {/* expose sendMove for the board component to call */}
      <input
        placeholder="UCI move (e.g. e2e4)"
        onKeyDown={e => { if (e.key === 'Enter') { sendMove((e.target as HTMLInputElement).value); (e.target as HTMLInputElement).value = '' } }}
      />
    </div>
  )
}

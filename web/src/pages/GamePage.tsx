import { useCallback } from 'react'
import { useNavigate } from 'react-router-dom'
import { useGameSocket } from '@/hooks/useGameSocket'
import { useCountdownClocks } from '@/hooks/useCountdownClocks'
import { useGameStore } from '@/store/game'
import Board from '@/components/Board'
import MoveList from '@/components/MoveList'
import type { ServerMessage, PlayerColor, GameResult, GameEndReason } from '@/types/protocol'

// TODO: replace layout and controls with designed UI components
export default function GamePage() {
  const navigate = useNavigate()
  const store    = useGameStore()

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

  function sendMove(uci: string) { send({ type: 'move',       uci }) }
  function resign()               { send({ type: 'resign'         }) }
  function offerDraw()            { send({ type: 'offer_draw'     }) }
  function acceptDraw()           { send({ type: 'accept_draw'    }) }
  function declineDraw()          { send({ type: 'decline_draw'   }) }
  function abort()                { send({ type: 'abort'          }) }

  // No active game → go back to lobby
  if (!store.wsPath) {
    navigate('/lobby', { replace: true })
    return null
  }

  // Waiting for a human opponent to join (invite code flow)
  const isHumanGame = store.inviteCode && !store.inviteCode.startsWith('bot:')
  if (store.status === 'waiting' && isHumanGame && store.inviteCode) {
    return (
      // TODO: replace with designed waiting room (WaitingRoom screen)
      <div style={{ padding: 32, textAlign: 'center' }}>
        <h2>Waiting for opponent…</h2>
        <p>Share this code:</p>
        <code style={{ fontSize: 32, letterSpacing: 6 }}>{store.inviteCode}</code>
        <br />
        <button
          style={{ marginTop: 24 }}
          onClick={() => { store.reset(); navigate('/lobby') }}
        >
          Cancel
        </button>
      </div>
    )
  }

  const myColor  = store.myColor  ?? 'white'
  const isMyTurn = store.turn === myColor && store.status === 'ongoing'

  const clocks = useCountdownClocks(
    store.clocks,
    store.turn,
    store.status === 'ongoing',
  )

  return (
    // TODO: replace with designed game layout (Game screen)
    <div style={{ display: 'flex', gap: 24, padding: 24, maxWidth: 1000, margin: '0 auto' }}>

      {/* Board column */}
      <div style={{ flex: '0 0 auto' }}>
        {/* Opponent info */}
        <div style={{ marginBottom: 8, padding: '4px 8px', background: '#f0f0f0', borderRadius: 4 }}>
          Opponent
          {clocks && store.myColor && (
            <span style={{ float: 'right', fontVariantNumeric: 'tabular-nums' }}>
              {formatClock(store.myColor === 'white' ? clocks.black : clocks.white)}
            </span>
          )}
          {!store.opponentConnected && store.status === 'ongoing' && (
            <span style={{ color: '#e55', marginLeft: 8 }}>(disconnected)</span>
          )}
        </div>

        {store.fen ? (
          <Board
            fen={store.fen}
            myColor={myColor}
            lastMove={store.lastMove}
            isMyTurn={isMyTurn}
            onMove={sendMove}
          />
        ) : (
          <div style={{ width: 480, height: 480, background: '#ccc', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
            Connecting…
          </div>
        )}

        {/* Own info */}
        <div style={{ marginTop: 8, padding: '4px 8px', background: '#f0f0f0', borderRadius: 4 }}>
          You ({myColor})
          {clocks && store.myColor && (
            <span style={{ float: 'right', fontVariantNumeric: 'tabular-nums' }}>
              {formatClock(store.myColor === 'white' ? clocks.white : clocks.black)}
            </span>
          )}
        </div>
      </div>

      {/* Sidebar */}
      <div style={{ flex: 1, display: 'flex', flexDirection: 'column', gap: 16, minWidth: 200 }}>

        {/* Draw offer banner */}
        {store.drawOfferedBy && store.drawOfferedBy !== myColor && (
          <div style={{ padding: 12, background: '#fffbe6', border: '1px solid #ffe58f', borderRadius: 6 }}>
            <p style={{ margin: '0 0 8px' }}>Your opponent offers a draw.</p>
            <button onClick={acceptDraw}  style={{ marginRight: 8 }}>Accept</button>
            <button onClick={declineDraw}>Decline</button>
          </div>
        )}

        {/* Game over banner */}
        {store.status === 'ended' && (
          <div style={{ padding: 12, background: '#f6ffed', border: '1px solid #b7eb8f', borderRadius: 6 }}>
            <p style={{ margin: '0 0 8px', fontWeight: 600 }}>
              {gameResultLabel(store.result, myColor)}
            </p>
            <p style={{ margin: '0 0 8px', fontSize: 13, color: '#555' }}>
              {store.endReason}
            </p>
            <button onClick={() => { store.reset(); navigate('/lobby') }} style={{ marginRight: 8 }}>
              Play again
            </button>
            <button onClick={() => navigate('/history')}>View history</button>
          </div>
        )}

        {/* Move list */}
        <div style={{ flex: 1, border: '1px solid #eee', borderRadius: 6, padding: 8, overflow: 'hidden' }}>
          <MoveList moveUciHistory={store.moveUciHistory} />
        </div>

        {/* Action buttons */}
        {store.status === 'ongoing' && (
          <div style={{ display: 'flex', gap: 8, flexWrap: 'wrap' }}>
            <button onClick={resign}>Resign</button>
            <button onClick={offerDraw} disabled={store.drawOfferedBy === myColor}>
              {store.drawOfferedBy === myColor ? 'Draw offered' : 'Offer draw'}
            </button>
            {store.moveUciHistory.length < 2 && (
              <button onClick={abort}>Abort</button>
            )}
          </div>
        )}
      </div>
    </div>
  )
}

function formatClock(ms: number): string {
  const total = Math.max(0, Math.ceil(ms / 1000))
  const m = Math.floor(total / 60)
  const s = total % 60
  return `${m}:${s.toString().padStart(2, '0')}`
}

function gameResultLabel(result: string | null, myColor: string): string {
  if (result === 'draw')    return 'Draw'
  if (result === 'aborted') return 'Game aborted'
  if (result === myColor)   return 'You win!'
  return 'You lose'
}

import { create } from 'zustand'
import type { StateMessage, PlayerColor, GameResult, GameEndReason, ClockState } from '@/types/protocol'

export interface GameState {
  // connection
  wsPath: string | null
  connectionStatus: 'idle' | 'connecting' | 'open' | 'closed' | 'error'

  // identity
  myColor: PlayerColor | null
  inviteCode: string | null

  // board
  fen: string | null
  lastMove: string | null  // UCI
  turn: PlayerColor | null
  clocks: ClockState | null

  // game lifecycle
  status: 'waiting' | 'ongoing' | 'ended'
  drawOfferedBy: PlayerColor | null
  result: GameResult | null
  endReason: GameEndReason | null

  opponentConnected: boolean

  // actions
  startHumanGame: (path: string) => void
  startBotGame: (path: string) => void
  applyState: (msg: StateMessage) => void
  setJoined: (color: PlayerColor, inviteCode: string) => void
  setDrawOffered: (by: PlayerColor) => void
  setDrawDeclined: () => void
  setGameEnded: (result: GameResult, reason: GameEndReason) => void
  setOpponentConnected: (connected: boolean) => void
  setConnectionStatus: (s: GameState['connectionStatus']) => void
  reset: () => void
}

const initial: Omit<GameState, 'startHumanGame' | 'startBotGame' | 'applyState' | 'setJoined' | 'setDrawOffered' | 'setDrawDeclined' | 'setGameEnded' | 'setOpponentConnected' | 'setConnectionStatus' | 'reset'> = {
  wsPath: null,
  connectionStatus: 'idle',
  myColor: null,
  inviteCode: null,
  fen: null,
  lastMove: null,
  turn: null,
  clocks: null,
  status: 'waiting',
  drawOfferedBy: null,
  result: null,
  endReason: null,
  opponentConnected: false,
}

export const useGameStore = create<GameState>()((set) => ({
  ...initial,

  startHumanGame: (path) => set({ ...initial, wsPath: path, status: 'waiting' }),
  startBotGame:   (path) => set({ ...initial, wsPath: path, status: 'waiting' }),

  applyState: (msg) => set({
    fen: msg.fen,
    lastMove: msg.lastMove,
    turn: msg.turn,
    clocks: msg.clocks ?? null,
    status: msg.status === 'ongoing' ? 'ongoing' : 'ended',
  }),

  setJoined: (color, inviteCode) => set({ myColor: color, inviteCode }),

  setDrawOffered:  (by)    => set({ drawOfferedBy: by }),
  setDrawDeclined: ()      => set({ drawOfferedBy: null }),

  setGameEnded: (result, reason) => set({
    status: 'ended',
    result,
    endReason: reason,
    drawOfferedBy: null,
  }),

  setOpponentConnected: (connected) => set({ opponentConnected: connected }),
  setConnectionStatus:  (s)         => set({ connectionStatus: s }),

  reset: () => set(initial),
}))

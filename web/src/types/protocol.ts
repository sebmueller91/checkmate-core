// ── Shared ────────────────────────────────────────────────────────────────────

export interface ClockState {
  white: number  // ms remaining
  black: number
}

// ── Client → Server ───────────────────────────────────────────────────────────

export type DrawClaimRule = 'FIFTY_MOVE' | 'THREEFOLD'

export type ClientMessage =
  | { type: 'move'; uci: string }
  | { type: 'offer_draw' }
  | { type: 'accept_draw' }
  | { type: 'decline_draw' }
  | { type: 'resign' }
  | { type: 'claim_draw'; rule: DrawClaimRule }
  | { type: 'abort' }

// ── Server → Client ───────────────────────────────────────────────────────────

export type GameStatus = 'ongoing' | 'checkmate' | 'draw'
export type PlayerColor = 'white' | 'black'
export type DrawReason = 'fifty_move_rule' | 'threefold_repetition' | 'stalemate' | 'insufficient_material' | 'agreement' | 'fifty_move' | 'threefold'
export type GameResult = PlayerColor | 'draw' | 'aborted'
export type GameEndReason = 'checkmate' | 'timeout' | 'resign' | 'agreement' | 'abort' | 'fifty_move' | 'threefold' | DrawReason

export interface StateMessage {
  type: 'state'
  fen: string
  lastMove: string | null  // UCI notation, e.g. "e2e4"
  turn: PlayerColor
  status: GameStatus
  drawReason?: DrawReason
  winner?: PlayerColor
  clocks?: ClockState
}

export type ServerMessage =
  | StateMessage
  | { type: 'draw_offered'; by: PlayerColor }
  | { type: 'draw_declined' }
  | { type: 'game_ended'; result: GameResult; reason: GameEndReason }
  | { type: 'error'; message: string }
  | { type: 'joined'; color: PlayerColor; inviteCode: string }
  | { type: 'opponent_connected' }
  | { type: 'opponent_disconnected' }

// ── REST API ──────────────────────────────────────────────────────────────────

export interface AuthResponse {
  token: string
  userId: string
  email: string
}

export interface UserResponse {
  userId: string
  email: string
}

export interface GameSummary {
  id: string
  result: string
  pgn: string
  createdAt: string
}

// ── Game setup ────────────────────────────────────────────────────────────────

export type Difficulty = 'beginner' | 'easy' | 'medium' | 'hard' | 'expert'

export interface TimeControl {
  /** Initial time per side in seconds. null = no clock. */
  seconds: number | null
  increment: number  // seconds
  label: string
}

export const TIME_CONTROL_PRESETS: TimeControl[] = [
  { seconds: 60,   increment: 0,  label: 'Bullet 1+0' },
  { seconds: 180,  increment: 2,  label: 'Blitz 3+2' },
  { seconds: 300,  increment: 0,  label: 'Blitz 5+0' },
  { seconds: 600,  increment: 0,  label: 'Rapid 10+0' },
  { seconds: 900,  increment: 10, label: 'Rapid 15+10' },
  { seconds: 1800, increment: 0,  label: 'Classical 30+0' },
]

export const DIFFICULTY_DESCRIPTIONS: Record<Difficulty, string> = {
  beginner: 'Just learning',
  easy:     'Casual play',
  medium:   'A decent challenge',
  hard:     'Strong club player',
  expert:   'Club-level challenge',
}

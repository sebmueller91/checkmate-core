import { useState, useMemo } from 'react'
import { Chessboard } from 'react-chessboard'
import { Chess } from 'chess.js'
import type { Square } from 'chess.js'
import type { PlayerColor } from '@/types/protocol'

const LAST_MOVE_STYLE:  React.CSSProperties = { background: 'rgba(155, 199, 0, 0.41)' }
const LEGAL_MOVE_STYLE: React.CSSProperties = { background: 'rgba(255, 255, 0, 0.40)' }
const SELECTED_STYLE:   React.CSSProperties = { background: 'rgba(255, 255, 0, 0.70)' }

interface BoardProps {
  fen: string
  myColor: PlayerColor
  lastMove: string | null   // UCI e.g. "e2e4", "e7e8q"
  isMyTurn: boolean
  onMove: (uci: string) => void
}

type PendingPromotion = { from: Square; to: Square }

export default function Board({ fen, myColor, lastMove, isMyTurn, onMove }: BoardProps) {
  const [selected,          setSelected]          = useState<Square | null>(null)
  const [optionSquares,     setOptionSquares]      = useState<Record<string, React.CSSProperties>>({})
  const [pendingPromotion,  setPendingPromotion]   = useState<PendingPromotion | null>(null)

  // One read-only Chess instance per FEN position
  const chess = useMemo(() => new Chess(fen), [fen])

  function legalTargetsFrom(sq: Square): Square[] {
    return chess.moves({ verbose: true, square: sq }).map(m => m.to as Square)
  }

  function isPromotion(from: Square, to: Square): boolean {
    const piece = chess.get(from)
    if (!piece || piece.type !== 'p') return false
    return (piece.color === 'w' && to[1] === '8') ||
           (piece.color === 'b' && to[1] === '1')
  }

  function attemptMove(from: Square, to: Square) {
    setSelected(null)
    setOptionSquares({})
    if (isPromotion(from, to)) {
      setPendingPromotion({ from, to })
    } else {
      onMove(`${from}${to}`)
    }
  }

  function selectSquare(sq: Square) {
    const targets = legalTargetsFrom(sq)
    const styles: Record<string, React.CSSProperties> = { [sq]: SELECTED_STYLE }
    targets.forEach(t => { styles[t] = LEGAL_MOVE_STYLE })
    setSelected(sq)
    setOptionSquares(styles)
  }

  function handleSquareClick({ square }: { square: string; piece: { pieceType: string } | null }) {
    if (!isMyTurn) return
    const sq = square as Square
    const myChar = myColor[0] as 'w' | 'b'
    const piece = chess.get(sq)

    // Own piece clicked → select (or re-select a different piece)
    if (piece && piece.color === myChar) {
      selectSquare(sq)
      return
    }

    // Legal target clicked → move
    if (selected && optionSquares[sq]) {
      attemptMove(selected, sq)
      return
    }

    // Empty/opponent square with nothing selected → deselect
    setSelected(null)
    setOptionSquares({})
  }

  function handlePieceDrop({
    sourceSquare,
    targetSquare,
  }: {
    sourceSquare: string
    targetSquare: string | null
    piece: { isSparePiece: boolean; position: string; pieceType: string }
  }): boolean {
    if (!isMyTurn || !targetSquare) return false
    const from = sourceSquare as Square
    const to   = targetSquare  as Square

    const valid = legalTargetsFrom(from).includes(to)
    if (!valid) return false

    if (isPromotion(from, to)) {
      setPendingPromotion({ from, to })
      return false   // wait for piece selection before sending
    }

    onMove(`${from}${to}`)
    return true
  }

  // Build square highlight map: last move < selection/legal (selection wins)
  const squareStyles: Record<string, React.CSSProperties> = {}
  if (lastMove) {
    squareStyles[lastMove.slice(0, 2)] = LAST_MOVE_STYLE
    squareStyles[lastMove.slice(2, 4)] = LAST_MOVE_STYLE
  }
  Object.assign(squareStyles, optionSquares)

  return (
    <div style={{ position: 'relative' }}>
      <Chessboard
        options={{
          position: fen,
          boardOrientation: myColor,
          squareStyles,
          allowDragging: isMyTurn,
          canDragPiece: ({ piece }) => isMyTurn && piece.pieceType[0] === myColor[0],
          onSquareClick: handleSquareClick,
          onPieceDrop: handlePieceDrop,
          showNotation: true,
          animationDurationInMs: 150,
        }}
      />
      {pendingPromotion && (
        <PromotionPicker
          color={myColor}
          onPick={(promo) => {
            onMove(`${pendingPromotion.from}${pendingPromotion.to}${promo}`)
            setPendingPromotion(null)
          }}
          onCancel={() => setPendingPromotion(null)}
        />
      )}
    </div>
  )
}

const PROMO_PIECES = [
  { uci: 'q', label: 'Queen'  },
  { uci: 'r', label: 'Rook'   },
  { uci: 'b', label: 'Bishop' },
  { uci: 'n', label: 'Knight' },
] as const

function PromotionPicker({
  color,
  onPick,
  onCancel,
}: {
  color: PlayerColor
  onPick: (piece: string) => void
  onCancel: () => void
}) {
  // TODO: replace with designed piece icons
  const labels: Record<string, string> = {
    q: color === 'white' ? '♕' : '♛',
    r: color === 'white' ? '♖' : '♜',
    b: color === 'white' ? '♗' : '♝',
    n: color === 'white' ? '♘' : '♞',
  }

  return (
    <div style={{
      position: 'absolute', inset: 0, background: 'rgba(0,0,0,0.55)',
      display: 'flex', alignItems: 'center', justifyContent: 'center', zIndex: 10,
    }}>
      <div style={{
        background: '#fff', borderRadius: 8, padding: '12px 16px',
        display: 'flex', gap: 8, boxShadow: '0 4px 24px rgba(0,0,0,0.4)',
      }}>
        {PROMO_PIECES.map(({ uci, label }) => (
          <button
            key={uci}
            onClick={() => onPick(uci)}
            title={label}
            style={{
              fontSize: 40, lineHeight: 1, background: 'none', border: '2px solid transparent',
              borderRadius: 6, padding: '4px 10px', cursor: 'pointer',
            }}
            onMouseEnter={e => (e.currentTarget.style.borderColor = '#888')}
            onMouseLeave={e => (e.currentTarget.style.borderColor = 'transparent')}
          >
            {labels[uci]}
          </button>
        ))}
        <button
          onClick={onCancel}
          style={{ fontSize: 16, background: 'none', border: 'none', cursor: 'pointer', padding: '0 4px', color: '#888' }}
        >
          ✕
        </button>
      </div>
    </div>
  )
}

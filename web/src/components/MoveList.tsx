import { useEffect, useRef } from 'react'
import { Chess } from 'chess.js'
import type { Square } from 'chess.js'

function uciHistoryToSan(uciMoves: string[]): string[] {
  const chess = new Chess()
  const sans: string[] = []
  for (const uci of uciMoves) {
    try {
      const result = chess.move({
        from:      uci.slice(0, 2) as Square,
        to:        uci.slice(2, 4) as Square,
        promotion: uci[4] as 'q' | 'r' | 'b' | 'n' | undefined,
      })
      if (result) sans.push(result.san)
    } catch {
      break
    }
  }
  return sans
}

interface MoveListProps {
  moveUciHistory: string[]
}

export default function MoveList({ moveUciHistory }: MoveListProps) {
  const scrollRef = useRef<HTMLDivElement>(null)
  const sans = uciHistoryToSan(moveUciHistory)

  useEffect(() => {
    scrollRef.current?.scrollIntoView({ behavior: 'smooth' })
  }, [sans.length])

  if (sans.length === 0) {
    return (
      <div style={{ color: '#888', fontSize: 13, padding: '8px 4px' }}>
        No moves yet.
      </div>
    )
  }

  // Pair into move rows: [[1, 'e4', 'e5'], [2, 'd4', undefined], ...]
  const pairs: Array<[number, string, string | undefined]> = []
  for (let i = 0; i < sans.length; i += 2) {
    pairs.push([i / 2 + 1, sans[i]!, sans[i + 1]])
  }

  return (
    <div style={{ overflowY: 'auto', fontFamily: 'monospace', fontSize: 14, lineHeight: 1.6 }}>
      {pairs.map(([n, white, black]) => (
        // TODO: highlight current/last move with design colors
        <div key={n} style={{ display: 'flex', gap: 6, padding: '1px 4px' }}>
          <span style={{ color: '#888', minWidth: 28, textAlign: 'right' }}>{n}.</span>
          <span style={{ minWidth: 52 }}>{white}</span>
          {black !== undefined && <span>{black}</span>}
        </div>
      ))}
      <div ref={scrollRef} />
    </div>
  )
}

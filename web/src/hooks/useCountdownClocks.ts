import { useState, useEffect, useRef } from 'react'
import type { ClockState, PlayerColor } from '@/types/protocol'

/**
 * Counts down the active player's clock locally at 100 ms resolution.
 * Resets to the server's authoritative values whenever `serverClocks` changes
 * (i.e. after every move). Returns null when there is no clock.
 */
export function useCountdownClocks(
  serverClocks: ClockState | null,
  turn: PlayerColor | null,
  isRunning: boolean,
): ClockState | null {
  const [clocks, setClocks] = useState<ClockState | null>(serverClocks)

  // Mutable refs so the interval callback always sees fresh values without
  // being a dependency that would restart the interval every tick.
  const clocksRef   = useRef(clocks)
  const lastTickRef = useRef(Date.now())

  // Sync authoritative server values after each move
  useEffect(() => {
    clocksRef.current = serverClocks
    setClocks(serverClocks)
    lastTickRef.current = Date.now()
  }, [serverClocks])

  // Local countdown — only restarts when turn or running state changes
  useEffect(() => {
    if (!isRunning || !turn || !clocksRef.current) return

    const id = setInterval(() => {
      const now     = Date.now()
      const elapsed = now - lastTickRef.current
      lastTickRef.current = now

      const prev = clocksRef.current
      if (!prev) return

      const next: ClockState = {
        white: turn === 'white' ? Math.max(0, prev.white - elapsed) : prev.white,
        black: turn === 'black' ? Math.max(0, prev.black - elapsed) : prev.black,
      }
      clocksRef.current = next
      setClocks(next)
    }, 100)

    return () => clearInterval(id)
  }, [isRunning, turn])

  return clocks
}

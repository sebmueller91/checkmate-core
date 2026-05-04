import { useEffect, useRef, useCallback } from 'react'
import type { ServerMessage, ClientMessage } from '@/types/protocol'
import { useAuthStore } from '@/store/auth'

const WS_BASE = import.meta.env.VITE_WS_BASE ?? ''

type ConnectionStatus = 'connecting' | 'open' | 'closed' | 'error'

interface UseGameSocketOptions {
  onMessage: (msg: ServerMessage) => void
  onStatusChange?: (status: ConnectionStatus) => void
}

/**
 * Manages the WebSocket lifecycle for a game room.
 *
 * path examples:
 *   '/game/new?time=300&increment=2'
 *   '/game/A3K7PQ'
 *   '/game/bot?difficulty=medium&side=white'
 */
export function useGameSocket(path: string | null, options: UseGameSocketOptions) {
  const { onMessage, onStatusChange } = options
  const wsRef = useRef<WebSocket | null>(null)
  const token = useAuthStore(s => s.token)

  // Keep callbacks in refs so the effect doesn't re-run on every render
  const onMessageRef = useRef(onMessage)
  const onStatusRef  = useRef(onStatusChange)
  onMessageRef.current = onMessage
  onStatusRef.current  = onStatusChange

  useEffect(() => {
    if (!path) return

    const wsUrl = buildWsUrl(path, token)
    const ws = new WebSocket(wsUrl)
    wsRef.current = ws

    onStatusRef.current?.('connecting')

    ws.onopen  = () => onStatusRef.current?.('open')
    ws.onerror = () => onStatusRef.current?.('error')
    ws.onclose = () => { onStatusRef.current?.('closed'); wsRef.current = null }

    ws.onmessage = (event: MessageEvent<string>) => {
      try {
        const msg = JSON.parse(event.data) as ServerMessage
        onMessageRef.current(msg)
      } catch {
        // malformed frame — ignore
      }
    }

    return () => {
      ws.onopen = ws.onerror = ws.onclose = ws.onmessage = null
      ws.close()
    }
  }, [path, token])

  const send = useCallback((msg: ClientMessage) => {
    const ws = wsRef.current
    if (ws?.readyState === WebSocket.OPEN) {
      ws.send(JSON.stringify(msg))
    }
  }, [])

  return { send }
}

function buildWsUrl(path: string, token: string | null): string {
  const protocol = location.protocol === 'https:' ? 'wss:' : 'ws:'
  const host = WS_BASE || `${protocol}//${location.host}`
  const url = new URL(`${host}${path}`)
  // The server reads Authorization from the WS handshake header.
  // Browsers don't allow custom headers on WebSocket — pass token as a query param
  // and the server should accept ?token= as fallback (or use a cookie instead).
  // For now we append it and handle server-side when we add that support.
  if (token) url.searchParams.set('token', token)
  return url.toString()
}

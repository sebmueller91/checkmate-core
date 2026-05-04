package dev.smueller.checkmate.server

import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.server.testing.testApplication
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class GameIntegrationTest {

    private val json = Json { encodeDefaults = true; ignoreUnknownKeys = true }

    private fun clientMsg(msg: ClientMessage) = Frame.Text(json.encodeToString(msg))
    private suspend fun ReceiveChannel<Frame>.recv(): ServerMessage =
        json.decodeFromString((receive() as Frame.Text).readText())

    @Test
    fun scholarsMatePlaysToCheckmate() = testApplication {
        application { configureApp() }
        val client = createClient { install(WebSockets) }

        val codeReady = CompletableDeferred<String>()

        coroutineScope {
            val whiteJob = async {
                client.webSocket("/game/new") {
                    val joined = incoming.recv() as ServerMessage.Joined
                    codeReady.complete(joined.inviteCode)
                    incoming.recv() // initial snapshot
                    incoming.recv() // OpponentConnected

                    outgoing.send(clientMsg(ClientMessage.Move("e2e4")))
                    incoming.recv() // state after e4

                    incoming.recv() // state after e5
                    outgoing.send(clientMsg(ClientMessage.Move("d1h5")))
                    incoming.recv() // state after Qh5

                    incoming.recv() // state after Nc6
                    outgoing.send(clientMsg(ClientMessage.Move("f1c4")))
                    incoming.recv() // state after Bc4

                    incoming.recv() // state after Nf6
                    outgoing.send(clientMsg(ClientMessage.Move("h5f7")))
                    val finalState = incoming.recv() as ServerMessage.State
                    assertEquals("checkmate", finalState.status)
                    assertEquals("white", finalState.winner)

                    val gameEnded = incoming.recv() as ServerMessage.GameEnded
                    assertEquals("white", gameEnded.result)
                    assertEquals("checkmate", gameEnded.reason)
                }
            }

            val blackJob = async {
                val code = codeReady.await()
                client.webSocket("/game/$code") {
                    incoming.recv() as ServerMessage.Joined
                    incoming.recv() // initial snapshot

                    incoming.recv() // state after e4
                    outgoing.send(clientMsg(ClientMessage.Move("e7e5")))
                    incoming.recv() // state after e5

                    incoming.recv() // state after Qh5
                    outgoing.send(clientMsg(ClientMessage.Move("b8c6")))
                    incoming.recv() // state after Nc6

                    incoming.recv() // state after Bc4
                    outgoing.send(clientMsg(ClientMessage.Move("g8f6")))
                    incoming.recv() // state after Nf6

                    incoming.recv() // final state
                    incoming.recv() // game ended
                }
            }

            whiteJob.await()
            blackJob.await()
        }
    }

    @Test
    fun resignEndsGame() = testApplication {
        application { configureApp() }
        val client = createClient { install(WebSockets) }

        val codeReady = CompletableDeferred<String>()

        coroutineScope {
            val whiteJob = async {
                client.webSocket("/game/new") {
                    codeReady.complete((incoming.recv() as ServerMessage.Joined).inviteCode)
                    incoming.recv() // snapshot
                    incoming.recv() // OpponentConnected
                    outgoing.send(clientMsg(ClientMessage.Resign))
                    val ended = incoming.recv() as ServerMessage.GameEnded
                    assertEquals("black", ended.result)
                    assertEquals("resign", ended.reason)
                }
            }

            val blackJob = async {
                val code = codeReady.await()
                client.webSocket("/game/$code") {
                    incoming.recv() as ServerMessage.Joined
                    incoming.recv() // snapshot
                    val msg = incoming.recv() as ServerMessage.GameEnded
                    assertEquals("black", msg.result)
                    assertEquals("resign", msg.reason)
                }
            }

            whiteJob.await()
            blackJob.await()
        }
    }

    @Test
    fun roomNotFoundReturnsError() = testApplication {
        application { configureApp() }
        val client = createClient { install(WebSockets) }

        client.webSocket("/game/XXXXXX") {
            assertIs<ServerMessage.Error>(incoming.recv())
        }
    }

    @Test
    fun roomFullReturnsError() = testApplication {
        application { configureApp() }
        val client = createClient { install(WebSockets) }

        val codeReady = CompletableDeferred<String>()
        val blackJoined = CompletableDeferred<Unit>()
        val thirdDone = CompletableDeferred<Unit>()

        coroutineScope {
            val w = async {
                client.webSocket("/game/new") {
                    codeReady.complete((incoming.recv() as ServerMessage.Joined).inviteCode)
                    incoming.recv() // snapshot
                    incoming.recv() // OpponentConnected
                    thirdDone.await() // keep connection open until third client is done
                }
            }

            val b = async {
                val code = codeReady.await()
                client.webSocket("/game/$code") {
                    incoming.recv() as ServerMessage.Joined
                    incoming.recv() // snapshot
                    blackJoined.complete(Unit)
                    thirdDone.await() // stay connected
                }
            }

            // Wait until both are in the room before sending the third
            blackJoined.await()
            val code = codeReady.await()
            client.webSocket("/game/$code") {
                val msg = incoming.recv()
                assertIs<ServerMessage.Error>(msg)
                assertEquals("Room is full", (msg as ServerMessage.Error).message)
            }
            thirdDone.complete(Unit)

            w.await()
            b.await()
        }
    }
}

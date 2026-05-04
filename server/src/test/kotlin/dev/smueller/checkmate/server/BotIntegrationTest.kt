package dev.smueller.checkmate.server

import dev.smueller.checkmate.Position
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.server.application.install
import io.ktor.server.testing.testApplication
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class BotIntegrationTest {

    private val json = Json { encodeDefaults = true; ignoreUnknownKeys = true }

    private fun clientMsg(msg: ClientMessage) = Frame.Text(json.encodeToString(msg))
    private suspend fun ReceiveChannel<Frame>.recv(): ServerMessage =
        json.decodeFromString((receive() as Frame.Text).readText())

    /** Fake engine that always picks the first legal move. */
    private val fakeEngine: EngineService = object : EngineService {
        override val available = true
        override suspend fun bestMove(fen: String, movetimeMs: Int, skillLevel: Int): String =
            Position.fromFen(fen).legalMoves().first().toUci()
    }

    private fun testApp(block: suspend io.ktor.server.testing.ApplicationTestBuilder.() -> Unit) =
        testApplication {
            application {
                install(Koin) {
                    slf4jLogger()
                    modules(module {
                        single { RoomRegistry() }
                        single<EngineService> { fakeEngine }
                    })
                }
                configureWebSockets()
                configureRouting()
            }
            block()
        }

    @Test
    fun botRespondsAfterHumanMove() = testApp {
        val client = createClient { install(WebSockets) }

        client.webSocket("/game/bot?difficulty=medium") {
            val joined = incoming.recv() as ServerMessage.Joined
            assertEquals("white", joined.color)

            val snapshot = incoming.recv() as ServerMessage.State
            assertEquals("ongoing", snapshot.status)

            // Human (white) plays e2e4
            outgoing.send(clientMsg(ClientMessage.Move("e2e4")))

            val afterHuman = incoming.recv() as ServerMessage.State
            assertEquals("ongoing", afterHuman.status)
            assertEquals("black", afterHuman.turn)  // now black's turn (bot)

            // Bot (black) auto-replies
            val afterBot = incoming.recv() as ServerMessage.State
            assertEquals("ongoing", afterBot.status)
            assertEquals("white", afterBot.turn)  // bot played, back to white
        }
    }

    @Test
    fun botMovesFirstWhenHumanIsBlack() = testApp {
        val client = createClient { install(WebSockets) }

        client.webSocket("/game/bot?side=black") {
            incoming.recv() as ServerMessage.Joined  // Joined(color=black)
            incoming.recv()                          // snapshot (white to move)

            // Bot (white) triggers its first move automatically
            val afterBot = incoming.recv() as ServerMessage.State
            assertEquals("black", afterBot.turn)  // after bot's first move, black (human) to move
        }
    }

    @Test
    fun resignEndsGame() = testApp {
        val client = createClient { install(WebSockets) }

        client.webSocket("/game/bot") {
            incoming.recv() // Joined
            incoming.recv() // snapshot
            outgoing.send(clientMsg(ClientMessage.Resign))
            val ended = incoming.recv() as ServerMessage.GameEnded
            assertEquals("resign", ended.reason)
            assertEquals("black", ended.result)  // bot wins when human resigns
        }
    }

    @Test
    fun illegalMoveReturnsError() = testApp {
        val client = createClient { install(WebSockets) }

        client.webSocket("/game/bot") {
            incoming.recv() // Joined
            incoming.recv() // snapshot
            outgoing.send(clientMsg(ClientMessage.Move("a1h8")))
            assertIs<ServerMessage.Error>(incoming.recv())
        }
    }

    @Test
    fun engineUnavailableReturnsError() = testApplication {
        application {
            install(Koin) {
                slf4jLogger()
                modules(module {
                    single { RoomRegistry() }
                    single<EngineService> {
                        object : EngineService {
                            override val available = false
                            override suspend fun bestMove(fen: String, movetimeMs: Int, skillLevel: Int) =
                                error("unavailable")
                        }
                    }
                })
            }
            configureWebSockets()
            configureRouting()
        }
        val client = createClient { install(WebSockets) }
        client.webSocket("/game/bot") {
            val msg = incoming.recv()
            assertIs<ServerMessage.Error>(msg)
        }
    }
}

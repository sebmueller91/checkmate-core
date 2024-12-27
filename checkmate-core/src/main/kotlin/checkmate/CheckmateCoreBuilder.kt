package checkmate

import checkmate.di.checkmateModule
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CheckmateCoreBuilder : KoinComponent {
    private val checkmateCoreImpl: CheckmateCoreImpl by inject()

    init {
        startKoin {
            modules(checkmateModule)
        }
        Runtime.getRuntime().addShutdownHook(Thread {
            stopKoin()
        })
    }

    fun build(): CheckmateCore = checkmateCoreImpl
}
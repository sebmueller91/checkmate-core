package checkmate

import checkmate.di.checkmateCoreModule
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.java.KoinJavaComponent.inject

class CheckmateCoreBuilder {
    private var koinApplication: KoinApplication? = null

    fun build(): CheckmateCore {
        koinApplication?.close()

        koinApplication = startKoin {
            modules(checkmateCoreModule)
        }

        val checkmateCore: CheckmateCore by inject(CheckmateCore::class.java)
        return checkmateCore
    }

    fun close() {
        koinApplication?.close()
        koinApplication = null
    }
}
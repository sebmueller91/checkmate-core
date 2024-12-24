package checkmate.impl

import checkmate.CheckmateCore
import org.koin.dsl.module

internal val checkmateModule = module {
    single<CheckmateCore> { CheckmateCoreImpl() }
}
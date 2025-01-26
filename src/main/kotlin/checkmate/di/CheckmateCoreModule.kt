package checkmate.di

import checkmate.CheckmateCore
import checkmate.CheckmateCoreImpl
import org.koin.dsl.module

internal val checkmateCoreModule = module {
    single<CheckmateCore> { CheckmateCoreImpl() }
}
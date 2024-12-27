package checkmate.di

import checkmate.CheckmateCore
import checkmate.CheckmateCoreImpl
import org.koin.dsl.module

internal val checkmateModule = module {
    single<CheckmateCore> { CheckmateCoreImpl() }
}
package com.borabor.threeinonecompose.di

import com.borabor.threeinonecompose.data.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindCalculatorRepository(repositoryImpl: CalculatorRepositoryImpl): CalculatorRepository

    @Binds
    abstract fun bindUnitConverterRepository(repositoryImpl: UnitConverterRepositoryImpl): UnitConverterRepository

    @Binds
    abstract fun bindTimerRepository(repositoryImpl: TimerRepositoryImpl): TimerRepository

    @Binds
    abstract fun bindStopwatchRepository(repositoryImpl: StopwatchRepositoryImpl): StopwatchRepository

    @Binds
    abstract fun bindCountdownRepository(repositoryImpl: CountdownRepositoryImpl): CountdownRepository
}
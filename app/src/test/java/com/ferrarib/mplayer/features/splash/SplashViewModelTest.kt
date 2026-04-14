package com.ferrarib.mplayer.features.splash

import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SplashViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `emits NavigateToSongs after splash delay`() = runTest(dispatcher) {
        val viewModel = SplashViewModel()

        viewModel.events.test {
            viewModel.start()
            advanceTimeBy(SplashViewModel.SPLASH_DELAY_MS - 1)
            expectNoEvents()
            advanceTimeBy(1)
            assertEquals(SplashEvent.NavigateToSongs, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}

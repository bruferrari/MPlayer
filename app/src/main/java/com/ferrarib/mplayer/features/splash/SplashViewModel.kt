package com.ferrarib.mplayer.features.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor() : ViewModel() {

    private val _events = MutableSharedFlow<SplashEvent>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val events: SharedFlow<SplashEvent> = _events

    fun start() {
        viewModelScope.launch {
            delay(SPLASH_DELAY_MS)
            _events.emit(SplashEvent.NavigateToSongs)
        }
    }

    companion object {
        const val SPLASH_DELAY_MS: Long = 1_200L
    }
}

sealed interface SplashEvent {
    data object NavigateToSongs : SplashEvent
}

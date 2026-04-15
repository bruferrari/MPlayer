package com.ferrarib.mplayer.core.ui

import android.graphics.Rect

sealed interface DevicePosture {
    data object Normal : DevicePosture
    data class TableTop(val hingeBounds: Rect) : DevicePosture
    data class Book(val hingeBounds: Rect) : DevicePosture
}

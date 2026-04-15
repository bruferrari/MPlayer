package com.ferrarib.mplayer.core.ui

import android.app.Activity
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

fun devicePostureFlow(activity: Activity): Flow<DevicePosture> =
    WindowInfoTracker.getOrCreate(activity)
        .windowLayoutInfo(activity)
        .map { info ->
            val fold = info.displayFeatures
                .filterIsInstance<FoldingFeature>()
                .firstOrNull { it.state == FoldingFeature.State.HALF_OPENED }
                ?: return@map DevicePosture.Normal

            when (fold.orientation) {
                FoldingFeature.Orientation.HORIZONTAL -> DevicePosture.TableTop(fold.bounds)
                FoldingFeature.Orientation.VERTICAL -> DevicePosture.Book(fold.bounds)
                else -> DevicePosture.Normal
            }
        }

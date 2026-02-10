package com.sae.pymon.haptic

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator

object HapticManager {

    private var vibrator: Vibrator? = null

    fun init(context: Context) {
        if (vibrator != null) return
        vibrator = context.getSystemService(Vibrator::class.java)
    }

    fun tap() {
        vibrator?.let {
            if (!it.hasVibrator()) return
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                it.vibrate(VibrationEffect.createOneShot(80, 255))
            } else {
                @Suppress("DEPRECATION")
                it.vibrate(50)
            }
        }
    }

    fun error() {
        vibrator?.let {
            if (!it.hasVibrator()) return
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val mVibratePattern = longArrayOf(0, 1000, 1000, 50)
                it.vibrate(VibrationEffect.createWaveform(mVibratePattern, -1))
            } else {
                @Suppress("DEPRECATION")
                it.vibrate(longArrayOf(0, 40, 30, 40), -1)
            }
        }
    }
}

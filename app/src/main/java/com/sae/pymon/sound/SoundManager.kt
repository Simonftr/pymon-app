package com.sae.pymon.sound

import android.content.Context
import android.media.SoundPool
import com.sae.pymon.R

object SoundManager {

    private var soundPool: SoundPool? = null
    private val sounds = mutableMapOf<String, Int>()

    fun init(context: Context) {
        if (soundPool != null) return

        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .build()

        sounds["b"] = soundPool!!.load(context, R.raw.sound1, 1)
        sounds["r"] = soundPool!!.load(context, R.raw.sound1, 1)
        sounds["v"] = soundPool!!.load(context, R.raw.sound1, 1)
        sounds["j"] = soundPool!!.load(context, R.raw.sound1, 1)
        sounds["error"] = soundPool!!.load(context, R.raw.sound1, 1)
    }

    fun play(code: String) {
        sounds[code]?.let {
            soundPool?.play(it, 1f, 1f, 1, 0, 1f)
        }
    }
}

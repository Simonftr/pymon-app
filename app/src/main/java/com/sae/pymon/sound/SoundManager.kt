package com.sae.pymon.sound

import android.content.Context
import android.media.SoundPool
import com.sae.pymon.R
import com.sae.pymon.sound.SoundManager.soundPool

object SoundManager {

    private var soundPool: SoundPool? = null
    private val sounds = mutableMapOf<String, Int>()

    fun init(context: Context) {
        if (soundPool != null) return

        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .build()

        sounds["b"] = soundPool!!.load(context, R.raw.bleu, 1)
        sounds["r"] = soundPool!!.load(context, R.raw.rouge, 1)
        sounds["v"] = soundPool!!.load(context, R.raw.vert, 1)
        sounds["j"] = soundPool!!.load(context, R.raw.jaune, 1)
        sounds["error"] = soundPool!!.load(context, R.raw.rouge, 1)
    }

    fun play(code: String) {
        sounds[code]?.let {
            soundPool?.play(it, 0.9f, 0.9f, 1, 0, 1f)
        }
    }
}

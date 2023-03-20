package com.capstone.Capstone2Project.utils.etc

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import com.capstone.Capstone2Project.R

fun playButtonSound(context: Context) {

    val audioAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_MEDIA)
        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        .build()

    val soundPool = SoundPool.Builder()
        .setMaxStreams(1)
        .setAudioAttributes(audioAttributes)
        .build()

    val sound = soundPool.load(context, R.raw.button_sound, 1)

    soundPool.play(sound, 1f, 1f, 0, 0,1f)
    soundPool.release()
}

fun muteBeepSound(context: Context) {
    val manager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    manager.apply {
        adjustVolume(AudioManager.ADJUST_MUTE,AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE)
    }
}

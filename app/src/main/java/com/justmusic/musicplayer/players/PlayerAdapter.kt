/*
Copyright 2022 Vasim Mansuri

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.justmusic.musicplayer.players

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.media.AudioManager.OnAudioFocusChangeListener
import android.support.v4.media.MediaMetadataCompat
import com.justmusic.utils.PrintLog.printLogD

/**
 * Abstract player implementation that handles playing music with proper handling of headphones
 * @param context Required to pass the context
 * and audio focus.
 */
abstract class PlayerAdapter(context: Context) {
    private val mApplicationContext: Context
    private val mAudioManager: AudioManager
    private val mAudioFocusHelper: AudioFocusHelper
    private var mPlayOnAudioFocus = false

    /**
     * Public methods for handle the NOISY broadcast and AudioFocus
     */
    fun play() {
        if (mAudioFocusHelper.requestAudioFocus()) {
            registerAudioNoisyReceiver()
            onPlay()
        }
    }

    fun stop() {
        mAudioFocusHelper.abandonAudioFocus()
        unregisterAudioNoisyReceiver()
        onStop()
    }

    fun pause() {
        if (!mPlayOnAudioFocus) {
            mAudioFocusHelper.abandonAudioFocus()
        }
        unregisterAudioNoisyReceiver()
        onPause()
    }

    /**
     * Abstract methods for responding to playback changes in the class that extends this one
     */
    protected abstract fun onPlay()
    protected abstract fun onPause()
    abstract fun playFromMedia(metadata: MediaMetadataCompat)
    abstract val currentMedia: MediaMetadataCompat?
    abstract val isPlaying: Boolean
    protected abstract fun onStop()
    abstract fun seekTo(position: Long)
    abstract fun setVolume(volume: Float)
    private var mAudioNoisyReceiverRegistered = false
    private val mAudioNoisyReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (AudioManager.ACTION_AUDIO_BECOMING_NOISY == intent.action) {
                if (isPlaying) {
                    pause()
                }
            }
        }
    }

    private fun registerAudioNoisyReceiver() {
        if (!mAudioNoisyReceiverRegistered) {
            mApplicationContext.registerReceiver(mAudioNoisyReceiver, AUDIO_NOISY_INTENT_FILTER)
            mAudioNoisyReceiverRegistered = true
        }
    }

    private fun unregisterAudioNoisyReceiver() {
        if (mAudioNoisyReceiverRegistered) {
            mApplicationContext.unregisterReceiver(mAudioNoisyReceiver)
            mAudioNoisyReceiverRegistered = false
        }
    }

    /**
     * Helper class for managing audio focus related tasks.
     */
    private inner class AudioFocusHelper : OnAudioFocusChangeListener {
        fun requestAudioFocus(): Boolean {
            @Suppress("DEPRECATION") val result = mAudioManager.requestAudioFocus(
                this,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN
            )
            return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        }

        fun abandonAudioFocus() {
            @Suppress("DEPRECATION")
            mAudioManager.abandonAudioFocus(this)
        }

        override fun onAudioFocusChange(focusChange: Int) {
            when (focusChange) {
                AudioManager.AUDIOFOCUS_GAIN -> {
                    printLogD(TAG, "onAudioFocusChange: AUDIOFOCUS_GAIN")
                    if (mPlayOnAudioFocus && !isPlaying) {
                        play()
                    } else if (isPlaying) {
                        setVolume(MEDIA_VOLUME_DEFAULT)
                    }
                    mPlayOnAudioFocus = false
                }
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                    printLogD(TAG, "onAudioFocusChange: AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK")
                    setVolume(MEDIA_VOLUME_DUCK)
                }
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                    printLogD(TAG, "onAudioFocusChange: AUDIOFOCUS_LOSS_TRANSIENT")
                    if (isPlaying) {
                        mPlayOnAudioFocus = true
                        pause()
                    }
                }
                AudioManager.AUDIOFOCUS_LOSS -> {
                    printLogD(TAG, "onAudioFocusChange: AUDIOFOCUS_LOSS")
                    @Suppress("DEPRECATION")
                    mAudioManager.abandonAudioFocus(this)
                    mPlayOnAudioFocus = false
                    //                    stop(); // stop will 'hard-close' everything
                    pause()
                }
            }
        }
    }

    companion object {
        private const val TAG = "PlayerAdapter"
        private const val MEDIA_VOLUME_DEFAULT = 1.0f
        private const val MEDIA_VOLUME_DUCK = 0.2f

        /**
         * NOISY broadcast receiver stuff
         */
        private val AUDIO_NOISY_INTENT_FILTER =
            IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
    }

    init {
        mApplicationContext = context.applicationContext
        mAudioManager = mApplicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        mAudioFocusHelper = AudioFocusHelper()
    }
}

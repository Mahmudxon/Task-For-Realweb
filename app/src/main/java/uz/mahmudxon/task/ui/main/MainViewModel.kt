package uz.mahmudxon.task.ui.main

import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor() : ViewModel() {
    private val volume1 = MutableLiveData<Float>()
    private val volume2 = MutableLiveData<Float>()
    private val timer = MutableLiveData<Int>()
    val player2 = MutableLiveData<Boolean>()
    fun getVolume1(): LiveData<Float> = volume1
    fun getVolume2(): LiveData<Float> = volume2
    fun getTimer(): LiveData<Int> = timer
    private var n = 0
    private var player1IsPlay = false

    private var maxVolume = 100F
    var smooth1 = 0
    var smooth2 = 0

    fun setMaxVolume(max: Float) {
        maxVolume = max
        viewModelScope.launch {
            timer()
        }
    }

    private suspend fun setVolume2Players(dur1: Int, pos1: Int, dur2: Int, pos2: Int) {
        volume1.value = getVolume(smooth1, pos1, dur1)
        volume2.value = getVolume(smooth2, pos2, dur2)
    }


    private suspend fun getVolume(smooth: Int, pos: Int, dur: Int): Float {
        return when {
            pos < smooth -> pos * maxVolume / smooth
            dur - pos < smooth -> (maxVolume - (dur - pos)) * maxVolume / smooth
            else -> maxVolume
        }
    }

    fun setPlayersInfo(
        player1Position: Long,
        player1Duration: Long,
        player2Position: Long,
        player2Duration: Long
    ) {
        viewModelScope.launch {
            setVolume2Players(
                (player1Duration / 1000).toInt(),
                (player1Position / 1000).toInt(),
                (player2Duration / 1000).toInt(),
                (player2Position / 1000).toInt()
            )
        }
    }

    inner class TextWatcher1 : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {

        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            smooth1 = try {
                p0?.toString()?.toInt() ?: 0
            } catch (e: Exception) {
                0
            }
        }
    }

    inner class TextWatcher2 : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {

        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            smooth2 = try {
                p0?.toString()?.toInt() ?: 0
            } catch (e: Exception) {
                0
            }
        }
    }

    private suspend fun timer() {
        timer.value = n++
        delay(1000)
        timer()
    }

    inner class Player1Listener : Player.EventListener {
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            super.onPlayerStateChanged(playWhenReady, playbackState)
            player1IsPlay = playWhenReady
            if (playbackState == ExoPlayer.STATE_ENDED) {
                player2.value = false
            }
        }
    }

    inner class Player2Listener : Player.EventListener {
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            super.onPlayerStateChanged(playWhenReady, playbackState)
            if (playbackState == ExoPlayer.STATE_ENDED) {
                player2.value = player1IsPlay
            }
        }
    }
}
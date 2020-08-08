package uz.mahmudxon.task.ui.main

import android.content.Context.AUDIO_SERVICE
import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_main.*
import uz.mahmudxon.task.R

@AndroidEntryPoint
class MainFragment : Fragment(R.layout.fragment_main), View.OnKeyListener {

    private val viewModel: MainViewModel by viewModels()

    private lateinit var player1: SimpleExoPlayer
    private lateinit var player2: SimpleExoPlayer

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.isFocusableInTouchMode = true
        view.requestFocus()
        view.setOnKeyListener(this)
        setPhoneVolume()
        player1 = ExoPlayerFactory.newSimpleInstance(context)
        player2 = ExoPlayerFactory.newSimpleInstance(context)
        controllerView1?.player = player1
        controllerView2?.player = player2
        preparePlayers()
        setObserver()
        edt1?.addTextChangedListener(viewModel.TextWatcher1())
        edt2?.addTextChangedListener(viewModel.TextWatcher2())
        edt1.setOnKeyListener(this)
        edt2.setOnKeyListener(this)
        viewModel.setMaxVolume(player1.volume / 2)
        player1.addListener(viewModel.Player1Listener())
        player2.addListener(viewModel.Player2Listener())
    }

    private fun setObserver() {
        viewModel.getVolume1()
            .observe(viewLifecycleOwner, Observer { player1.volume = it })
        viewModel.getVolume2()
            .observe(viewLifecycleOwner, Observer { player2.volume = it })
        viewModel.getTimer().observe(viewLifecycleOwner, Observer {
            viewModel.setPlayersInfo(
                player1.currentPosition,
                player1.duration,
                player2.currentPosition,
                player2.duration
            )
        })
        viewModel.player2.observe(viewLifecycleOwner, Observer {
            if (it) player2.seekTo(0L)
            player2.playWhenReady = it
        })
    }

    private fun setPhoneVolume() {
        val audioManger = context?.getSystemService(AUDIO_SERVICE) as AudioManager
        audioManger.setStreamVolume(
            AudioManager.STREAM_MUSIC,
            audioManger.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
            0
        )
    }

    private fun preparePlayers() {
        val url1 =
            "https://uzhits.net/music/dl2/2020/04/01/123777janob_rasul_-_bomba_(uzhits.net).mp3"
        val url2 =
            "https://uzhits.net/music/dl2/2020/06/17/farrux_xamrayev_-_otajon_namoz_oqing_endi_(uzhits.net).mp3"
        val dataSource =
            DefaultDataSourceFactory(context, Util.getUserAgent(context, "HomeWork :-)"))
        val mediaSource = ExtractorMediaSource.Factory(dataSource)
            .setExtractorsFactory(DefaultExtractorsFactory())
        player1.prepare(mediaSource.createMediaSource(Uri.parse(url1)))
        player2.prepare(mediaSource.createMediaSource(Uri.parse(url2)))
    }

    override fun onKey(p0: View?, p1: Int, p2: KeyEvent?): Boolean {
        return p1 == KeyEvent.KEYCODE_VOLUME_DOWN || p1 == KeyEvent.KEYCODE_VOLUME_UP
    }

}
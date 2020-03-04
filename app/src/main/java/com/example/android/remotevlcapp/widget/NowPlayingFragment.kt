package com.example.android.remotevlcapp.widget

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.android.core.result.Result
import com.example.android.model.VLCPlayer
import com.example.android.remotevlcapp.R
import com.example.android.remotevlcapp.ui.MainActivityViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.*
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_now_playing.*
import javax.inject.Inject

class NowPlayingFragment : DaggerFragment(),
    MediaSlider.OnProgressChangeListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: MainActivityViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(requireActivity(), viewModelFactory)
            .get(MainActivityViewModel::class.java)

        return inflater.inflate(R.layout.fragment_now_playing, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val nowPlayingSheet = view!!.findViewById<View>(R.id.now_playing_sheet)
        val behavior = BottomSheetBehavior.from(nowPlayingSheet).apply {
            state = STATE_HIDDEN
        }

        viewModel.status.observe(viewLifecycleOwner, Observer {
            if (it is Result.Success) {
                val status = it.data
                title.text = status.filename?.toUri()?.lastPathSegment
                media_slider.setMaxProgress(status.length)
                media_slider.setProgress(status.time)

                when (status.state) {
                    VLCPlayer.State.STOPPED -> {
                        behavior.state = STATE_HIDDEN
                        playPause.isPlaying = false
                    }
                    VLCPlayer.State.PAUSED -> {
                        if (behavior.state == STATE_HIDDEN) {
                            behavior.state = STATE_COLLAPSED
                        }
                        playPause.isPlaying = false
                    }
                    VLCPlayer.State.PLAYING -> {
                        if (behavior.state == STATE_HIDDEN) {
                            behavior.state = STATE_COLLAPSED
                        }
                        playPause.isPlaying = true
                    }
                }

                if (status.loop || status.repeat) {
                    repeat.mode = if (status.loop) RepeatButton.Mode.LOOP
                    else RepeatButton.Mode.ONE
                } else {
                    repeat.mode = RepeatButton.Mode.OFF
                }

                fullscreen.highlight = status.fullscreen
                shuffle.highlight = status.random
            } else {
                behavior.state = STATE_HIDDEN
            }
        })

        title.setOnClickListener {
            when (behavior.state) {
                STATE_COLLAPSED -> behavior.state = STATE_EXPANDED
                STATE_EXPANDED -> behavior.state = STATE_COLLAPSED
            }
        }

        media_slider.setOnProgressChangeListener(this)

        stop.setOnClickListener {
            viewModel.sendCommand(VLCPlayer.Command.STOP)
        }

        playPause.setOnClickListener {
            if (playPause.isPlaying) viewModel.sendCommand(VLCPlayer.Command.PAUSE)
            else viewModel.sendCommand(VLCPlayer.Command.PLAY)
        }

        next.setOnClickListener {
            viewModel.sendCommand(VLCPlayer.Command.NEXT)
        }

        repeat.setOnClickListener {
            when (repeat.mode) {
                RepeatButton.Mode.ONE,
                RepeatButton.Mode.LOOP -> viewModel.sendCommand(VLCPlayer.Command.REPEAT)
                RepeatButton.Mode.OFF -> viewModel.sendCommand(VLCPlayer.Command.LOOP)
            }
        }

        shuffle.setOnClickListener {
            viewModel.sendCommand(VLCPlayer.Command.RANDOM)
        }

        fullscreen.setOnClickListener {
            viewModel.sendCommand(VLCPlayer.Command.FULLSCREEN)
        }

        fast_forward.setOnClickListener {
            viewModel.sendCommand(VLCPlayer.Command.SEEK, "+15S")
        }

        rewind.setOnClickListener {
            viewModel.sendCommand(VLCPlayer.Command.SEEK, "-15S")
        }
    }

    override fun onProgressChanged(progress: Int) {
        viewModel.sendCommand(VLCPlayer.Command.SEEK, progress.toString())
    }
}
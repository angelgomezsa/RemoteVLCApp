package com.example.android.remotevlcapp.widget

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.example.android.core.result.Result
import com.example.android.model.VLCPlayer
import com.example.android.remotevlcapp.R
import com.example.android.remotevlcapp.ui.MainActivityViewModel
import com.example.android.remotevlcapp.widget.BottomSheetBehavior.Companion.STATE_HIDDEN
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_now_playing.*
import javax.inject.Inject

class NowPlayingFragment : DaggerFragment(), MediaSeekBar.OnProgressChangeListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: MainActivityViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_now_playing, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(requireActivity(), viewModelFactory)
            .get(MainActivityViewModel::class.java)

//        val behavior = BottomSheetBehavior.from<View>(view!!.findViewById(R.id.now_playing_sheet))
        val behavior = BottomSheetBehavior.from(view!!.findViewById(R.id.now_playing_sheet))

        behavior.state = STATE_HIDDEN

        viewModel.status.observe(viewLifecycleOwner, Observer {
            if (it is Result.Success) {
                val status = it.data
                title.text = status.information?.category?.meta?.filename
                mediaSeekBar.setMaxProgress(status.length)
                mediaSeekBar.setProgress(status.time)
                mediaSeekBar.setOnProgressChangeListener(this)

                // There seems to be a bug when you set isHideable property to false
                // it shows the expanded state for a moment and then goes back to collapsed
                // state - using BottomSheetBehavior implementation from
                // the Google I/O 2019 app solves the bug - update pending.
                when (status.state) {
                    VLCPlayer.State.STOPPED -> {
                        behavior.isHideable = true
                        behavior.state = STATE_HIDDEN
                        playPause.isPlaying = false
                    }
                    VLCPlayer.State.PAUSED -> {
                        behavior.isHideable = false
                        playPause.isPlaying = false
                    }
                    VLCPlayer.State.PLAYING -> {
                        behavior.isHideable = false
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
                behavior.isHideable = true
                behavior.state = STATE_HIDDEN
            }
        })

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
package com.example.android.remotevlcapp.ui.remote

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.android.core.result.Result
import com.example.android.model.HostInfo
import com.example.android.model.Status
import com.example.android.model.VLCPlayer
import com.example.android.remotevlcapp.R
import com.example.android.remotevlcapp.ui.MainNavigationFragment
import com.example.android.remotevlcapp.widget.MediaSlider
import com.example.android.remotevlcapp.widget.RepeatButton
import kotlinx.android.synthetic.main.fragment_remote.*
import kotlinx.android.synthetic.main.media_slider.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


class RemoteFragment : MainNavigationFragment(),
    MediaSlider.OnProgressChangeListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: RemoteViewModel
    private var currentHost: HostInfo? = null

    private var isVolumeDownPressed = false
    private var isVolumeUpPressed = false
    private val remoteScope = CoroutineScope(Dispatchers.Main)


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this, viewModelFactory).get(RemoteViewModel::class.java)
        return inflater.inflate(R.layout.fragment_remote, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        media_slider.setOnProgressChangeListener(this)

        viewModel.currentHost.observe(viewLifecycleOwner, Observer {
            currentHost = it
            setConnecting(it)
        })

        viewModel.status.observe(viewLifecycleOwner, Observer {
            if (it is Result.Success) {
                setRemoteUi(it.data)
                motion_container.transitionToEnd()
            } else {
                motion_container.transitionToStart()
                currentHost?.let { host ->
                    setConnecting(host)
                }
            }
        })

        /** NAVIGATION */

        nav_up.setOnClickListener {
            viewModel.sendCommand(VLCPlayer.Command.KEY, VLCPlayer.KeyCommands.NAVIGATE_UP)
        }

        nav_right.setOnClickListener {
            viewModel.sendCommand(VLCPlayer.Command.KEY, VLCPlayer.KeyCommands.NAVIGATE_RIGHT)
        }

        nav_down.setOnClickListener {
            viewModel.sendCommand(VLCPlayer.Command.KEY, VLCPlayer.KeyCommands.NAVIGATE_DOWN)
        }

        nav_left.setOnClickListener {
            viewModel.sendCommand(VLCPlayer.Command.KEY, VLCPlayer.KeyCommands.NAVIGATE_LEFT)
        }

        nav_click.setOnClickListener {
            viewModel.sendCommand(VLCPlayer.Command.KEY, VLCPlayer.KeyCommands.NAVIGATE_ACTIVATE)
        }

        nav_back.setOnClickListener {
            viewModel.sendCommand(VLCPlayer.Command.KEY, VLCPlayer.KeyCommands.NAVIGATE_BACK)
        }

        /** TOP BAR */

        audiotrack.setOnClickListener {
            viewModel.sendCommand(VLCPlayer.Command.KEY, VLCPlayer.KeyCommands.AUDIO)
        }

        subtitles.setOnClickListener {
            viewModel.sendCommand(VLCPlayer.Command.KEY, VLCPlayer.KeyCommands.SUBTITLE)
        }

        stop.setOnClickListener {
            viewModel.sendCommand(VLCPlayer.Command.STOP)
        }

        fullscreen.setOnClickListener {
            viewModel.sendCommand(VLCPlayer.Command.FULLSCREEN)
        }

        aspect_ratio.setOnClickListener {
            viewModel.sendCommand(VLCPlayer.Command.KEY, VLCPlayer.KeyCommands.ASPECT_RATIO)
        }

        /** PLAYBACK */

        previous.setOnClickListener {
            viewModel.sendCommand(VLCPlayer.Command.PREVIOUS)
        }

        rewind.setOnClickListener {
            viewModel.sendCommand(VLCPlayer.Command.SEEK, "-15S")
        }

        play_pause.setOnClickListener {
            if (play_pause.isPlaying) viewModel.sendCommand(VLCPlayer.Command.PAUSE)
            else viewModel.sendCommand(VLCPlayer.Command.PLAY)
        }

        fast_forward.setOnClickListener {
            viewModel.sendCommand(VLCPlayer.Command.SEEK, "+15S")
        }

        next.setOnClickListener {
            viewModel.sendCommand(VLCPlayer.Command.NEXT)
        }

        volume_down.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    isVolumeDownPressed = true
                    remoteScope.launch {
                        while (isVolumeDownPressed) {
                            viewModel.sendCommand(
                                VLCPlayer.Command.KEY,
                                VLCPlayer.KeyCommands.VOLUME_DOWN
                            )
                            delay(250)
                        }
                    }
                    false
                }
                MotionEvent.ACTION_UP -> {
                    isVolumeDownPressed = false
                    false
                }
                else -> false
            }
        }

        mute.setOnClickListener {
            viewModel.sendCommand(VLCPlayer.Command.KEY, VLCPlayer.KeyCommands.MUTE)
        }

        volume_up.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    isVolumeUpPressed = true
                    remoteScope.launch {
                        while (isVolumeUpPressed) {
                            viewModel.sendCommand(
                                VLCPlayer.Command.KEY,
                                VLCPlayer.KeyCommands.VOLUME_UP
                            )
                            delay(250)
                        }
                    }
                    false
                }
                MotionEvent.ACTION_UP -> {
                    isVolumeUpPressed = false
                    false
                }
                else -> false
            }
        }

        random.setOnClickListener {
            viewModel.sendCommand(VLCPlayer.Command.RANDOM)
        }

        repeat.setOnClickListener {
            when (repeat.mode) {
                RepeatButton.Mode.ONE,
                RepeatButton.Mode.LOOP -> viewModel.sendCommand(VLCPlayer.Command.REPEAT)
                RepeatButton.Mode.OFF -> viewModel.sendCommand(VLCPlayer.Command.LOOP)
            }
        }
    }

    private fun setRemoteUi(status: Status) {
        media_slider.setMaxProgress(status.length)
        media_slider.setProgress(status.time)

        when (status.state) {
            VLCPlayer.State.STOPPED -> {
                play_pause.isPlaying = false
                media_slider.slider.isEnabled = false
            }
            VLCPlayer.State.PAUSED -> {
                play_pause.isPlaying = false
                media_slider.slider.isEnabled = true
            }
            VLCPlayer.State.PLAYING -> {
                play_pause.isPlaying = true
                media_slider.slider.isEnabled = true
            }
        }

        if (status.loop || status.repeat) {
            repeat.mode = if (status.loop) RepeatButton.Mode.LOOP
            else RepeatButton.Mode.ONE
        } else {
            repeat.mode = RepeatButton.Mode.OFF
        }

        random.highlight = status.random
        fullscreen.highlight = status.fullscreen

        val filename = status.filename
        info1.text = context?.getString(R.string.now_playing)
        info2.text = filename?.toUri()?.lastPathSegment
            ?: context?.getString(R.string.nothing_playing)
    }

    private fun setConnecting(host: HostInfo) {
        info1.text = context?.getString(R.string.connecting)
        info2.text = context?.getString(R.string.connecting_to, host.name, host.address)
    }

    override fun onProgressChanged(progress: Int) {
        viewModel.sendCommand(VLCPlayer.Command.SEEK, progress.toString())
    }
}


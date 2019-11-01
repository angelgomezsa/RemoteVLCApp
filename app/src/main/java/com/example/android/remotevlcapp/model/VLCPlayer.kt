package com.example.android.remotevlcapp.model

/**
 *  Constants for the different VLC states and commands
 */
object VLCPlayer {

    object State {
        const val PLAYING = "playing"
        const val PAUSED = "paused"
        const val STOPPED = "stopped"
    }

    object Command {
        const val PLAY = "pl_forceresume"
        const val PAUSE = "pl_forcepause"
        const val STOP = "pl_stop"
        const val NEXT = "pl_next"
        const val PREVIOUS = "pl_previous"
        const val SEEK = "seek"
        const val RANDOM = "pl_random"
        const val REPEAT = "pl_repeat"
        const val LOOP = "pl_loop"
        const val FULLSCREEN = "fullscreen"
        const val VOLUME = "volume"
        const val KEY = "key"
    }

    object KeyCommands {
        const val NAVIGATE_UP = "nav-up"
        const val NAVIGATE_RIGHT = "nav-right"
        const val NAVIGATE_DOWN = "nav-down"
        const val NAVIGATE_LEFT = "nav-left"
        const val NAVIGATE_ACTIVATE = "nav-activate"
        const val NAVIGATE_BACK = "disc-menu"
        const val MUTE = "vol-mute"
        const val SUBTITLE = "subtitle-track"
        const val AUDIO = "audio-track"
        const val ASPECT_RATIO = "aspect-ratio"

    }
}
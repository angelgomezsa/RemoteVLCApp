package com.example.android.remotevlcapp.widget

import android.content.Context
import android.graphics.drawable.AnimatedVectorDrawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.content.res.ResourcesCompat
import com.example.android.remotevlcapp.R

class PlayPauseButton : AppCompatImageButton {

    private val playToPause = ResourcesCompat.getDrawable(
        context.resources,
        R.drawable.avd_play_to_pause,
        null
    ) as AnimatedVectorDrawable
    private val pauseToPlay = ResourcesCompat.getDrawable(
        context.resources,
        R.drawable.avd_pause_to_play,
        null
    ) as AnimatedVectorDrawable

    var isPlaying = false
        set(value) {
            if (value == field) return
            field = value
            val avd = if (value) playToPause else pauseToPlay
            setImageDrawable(avd)
            avd.start()
        }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )
}
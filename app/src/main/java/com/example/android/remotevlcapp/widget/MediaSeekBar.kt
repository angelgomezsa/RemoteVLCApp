package com.example.android.remotevlcapp.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.SeekBar
import androidx.appcompat.widget.LinearLayoutCompat
import com.example.android.remotevlcapp.R
import com.example.android.remotevlcapp.util.formatTime
import kotlinx.android.synthetic.main.media_seek_bar.view.*

class MediaSeekBar : LinearLayoutCompat {

    private var isTracking = false
    private var listener: OnProgressChangeListener? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.media_seek_bar, this, true)

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                timeText.text = formatTime(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                isTracking = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                listener?.onProgressChanged(seekBar.progress)
                isTracking = false
            }
        })
    }

    fun setProgress(progress: Int) {
        if (isTracking || seekBar.progress == progress) return
        seekBar.progress = progress
        timeText.text = formatTime(progress)
    }

    fun setMaxProgress(max: Int) {
        if (seekBar.max == max) return
        seekBar.max = max
        lengthText.text = formatTime(max)
    }

    fun setOnProgressChangeListener(onProgressChangeListener: OnProgressChangeListener) {
        listener = onProgressChangeListener
    }

    interface OnProgressChangeListener {
        fun onProgressChanged(progress: Int)
    }
}
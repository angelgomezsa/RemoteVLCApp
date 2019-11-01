package com.example.android.remotevlcapp.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.SeekBar
import androidx.appcompat.widget.LinearLayoutCompat
import com.example.android.remotevlcapp.R
import kotlinx.android.synthetic.main.volume_seek_bar.view.*
import kotlin.math.roundToInt

class VolumeSeekBar : LinearLayoutCompat {

    private var isTracking = false
    private var listener: OnVolumeChangeListener? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.volume_seek_bar, this, true)

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                volume_text.text = (progress / 2.56).roundToInt().toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                isTracking = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                listener?.onVolumeChanged(seekBar.progress)
                isTracking = false
            }
        })
    }

    fun setVolume(volume: Int) {
        if (isTracking || seekBar.progress == volume) return
        volume_text.text = volume.toString()
        seekBar.progress = volume
    }

    fun setOnVolumeChangeListener(onVolumeChangeListener: OnVolumeChangeListener) {
        listener = onVolumeChangeListener
    }

    interface OnVolumeChangeListener {
        fun onVolumeChanged(volume: Int)
    }

}
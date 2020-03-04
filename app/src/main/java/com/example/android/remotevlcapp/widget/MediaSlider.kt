package com.example.android.remotevlcapp.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.appcompat.widget.LinearLayoutCompat
import com.example.android.remotevlcapp.R
import com.example.android.remotevlcapp.util.formatTime
import com.google.android.material.slider.Slider
import kotlinx.android.synthetic.main.media_slider.view.*
import kotlin.math.roundToInt

class MediaSlider : LinearLayoutCompat {

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
        inflater.inflate(R.layout.media_slider, this, true)
        slider.setLabelFormatter { value -> formatTime(value.toInt()) }

        slider.addOnChangeListener { _, value, _ ->
            timeText.text = formatTime(value.roundToInt())
        }

        slider.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {
                isTracking = true
            }

            override fun onStopTrackingTouch(slider: Slider) {
                listener?.onProgressChanged(slider.value.roundToInt())
                isTracking = false
            }
        })

        timeText.text = formatTime(0)
        lengthText.text = formatTime(0)
    }

    fun setProgress(progress: Int) {
        val value = progress.toFloat()
        if (isTracking || slider.value == value) return
        slider.value = value
        timeText.text = formatTime(progress)
    }

    fun setMaxProgress(max: Int) {
        val value = max.toFloat()
        if (max == 0) {
            lengthText.text = formatTime(0)
            return
        } else if (slider.valueTo == value) return
        slider.valueTo = value
        lengthText.text = formatTime(max)
    }

    fun setOnProgressChangeListener(onProgressChangeListener: OnProgressChangeListener) {
        listener = onProgressChangeListener
    }

    interface OnProgressChangeListener {
        fun onProgressChanged(progress: Int)
    }
}
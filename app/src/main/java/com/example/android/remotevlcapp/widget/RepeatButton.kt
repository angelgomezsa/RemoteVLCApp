package com.example.android.remotevlcapp.widget

import android.content.Context
import android.util.AttributeSet
import com.example.android.remotevlcapp.R

class RepeatButton : HighlightButton {

    var mode: Mode = Mode.OFF
        set(value) {
            if (value == field) return
            field = value
            highlight = when (mode) {
                Mode.OFF -> {
                    setImageResource(R.drawable.ic_repeat)
                    false
                }
                Mode.ONE -> {
                    setImageResource(R.drawable.ic_repeat_one)
                    true
                }
                Mode.LOOP -> {
                    setImageResource(R.drawable.ic_repeat)
                    true
                }
            }
        }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    enum class Mode {
        OFF, ONE, LOOP
    }
}
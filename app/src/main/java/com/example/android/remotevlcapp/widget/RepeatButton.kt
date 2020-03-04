package com.example.android.remotevlcapp.widget

import android.content.Context
import android.util.AttributeSet
import com.example.android.remotevlcapp.R

class RepeatButton : HighlightMaterialButton {

    enum class Mode {
        OFF, ONE, LOOP
    }

    var mode: Mode = Mode.OFF
        set(value) {
            if (value == field) return
            field = value
            highlight = when (mode) {
                Mode.OFF -> {
                    setIconResource(R.drawable.ic_repeat)
                    false
                }
                Mode.ONE -> {
                    setIconResource(R.drawable.ic_repeat_one)
                    true
                }
                Mode.LOOP -> {
                    setIconResource(R.drawable.ic_repeat)
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
}
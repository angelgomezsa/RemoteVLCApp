package com.example.android.remotevlcapp.widget

import android.content.Context
import android.util.AttributeSet
import com.example.android.remotevlcapp.R
import com.google.android.material.button.MaterialButton

class PlayPauseMaterialButton : MaterialButton {

    var isPlaying = false
        set(value) {
            if (value == field) return
            field = value
            if (value) {
                setIconResource(R.drawable.ic_pause)
            } else {
                setIconResource(R.drawable.ic_play)
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
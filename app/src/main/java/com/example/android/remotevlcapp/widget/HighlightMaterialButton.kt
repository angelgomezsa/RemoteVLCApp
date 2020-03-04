package com.example.android.remotevlcapp.widget

import android.content.Context
import android.util.AttributeSet
import com.example.android.remotevlcapp.R
import com.google.android.material.button.MaterialButton

open class HighlightMaterialButton : MaterialButton {

    var highlight = false
        set(value) {
            if (value == field) return
            field = value
            if (highlight) setIconTintResource(R.color.highlight_button_color)
            else setIconTintResource(R.color.default_button_color)
        }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

}
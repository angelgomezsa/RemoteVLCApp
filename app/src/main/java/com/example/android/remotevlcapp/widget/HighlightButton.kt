package com.example.android.remotevlcapp.widget

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.content.res.ResourcesCompat
import com.example.android.remotevlcapp.R

open class HighlightButton : AppCompatImageButton {

    private val highlightColor =
        ResourcesCompat.getColor(context.resources, R.color.highlightButtonColor, context.theme)
    private val defaultColor =
        ResourcesCompat.getColor(context.resources, R.color.defaultButtonColor, context.theme)

    var highlight = false
        set(value) {
            if (value == field) return
            field = value
            if (highlight) setColorFilter(highlightColor)
            else setColorFilter(defaultColor)
        }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

}
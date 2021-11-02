package com.example.customfancontroller

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import java.lang.Double.min
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

//top-level enum to represent the available speeds.
//values are of type Int because the values are string resource rather than actual strings
private enum class FanSpeed(val label: Int) {
    OFF(R.string.fan_off),
    LOW(R.string.fan_low),
    MEDIUM(R.string.fan_medium),
    HIGH(R.string.fan_high);

//    extension function that changes the current fan speed to the next speed in the list
    fun next() = when (this) {
        OFF -> LOW
        LOW -> MEDIUM
        MEDIUM -> HIGH
        HIGH -> OFF
    }
}

//constants used as part of drawing the dial indicator and labels
private const val RADIUS_OFFSET_LABEL = 30
private const val RADIUS_OFFSET_INDICATOR = -35

class DialView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr){

//    define several variables you need in order to draw the custom view
//    radius of the circle
    private var radius = 0.0f
//    active selection
    private var fanSpeed = FanSpeed.OFF
//    position variable which will be used to draw label and indicator circle position
    private val pointPosition: PointF = PointF(0.0f, 0.0f)

//    initialize paint object with handful of basic styles.
//    these styles are initialized here to help speed up the drawing step
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create("", Typeface.BOLD)
    }

//    add init block. setting view's isClickable true enables that view to accept user input
    init {
        isClickable = true
    }

    override fun performClick(): Boolean {
//        super.performClick() must happen first, which enables accessibility events as
//        well as calls onClickListener()
        if (super.performClick()) return true
//        increment speed of fan with next()
        fanSpeed = fanSpeed.next()
//        set the view's content description to th estring resource representing
//        the current speed (off, 1, 2, 3)
        contentDescription = resources.getString(fanSpeed.label)
//        invalidate entire view, forcing call to onDraw() to redraw view. If something
//        in custom view changes for any reason, including user interaction, and change
//        needs to be displayed, call invalidate()
        invalidate()
        return true
    }

//    draw fan controller custom view onto screen

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        radius = (min(width, height) / 2.0 * 0.8).toFloat()
    }

//    define computeXYForSpeed() extension function for the PointF class
    private fun PointF.computeXYForSpeed(pos: FanSpeed, radius: Float) {
//        angles are in radians
        val startAngle = Math.PI * (9 / 8.0)
        val angle = startAngle + pos.ordinal * (Math.PI / 4)
        x = (radius * cos(angle)).toFloat() + width / 2
        y = (radius * sin(angle)).toFloat() + height / 2
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
//        set dial background color to green if selection not off
        paint.color = if (fanSpeed == FanSpeed.OFF) Color.GRAY else Color.GREEN

//        draw circle for dial
        canvas?.drawCircle((width/ 2).toFloat(), (height / 2).toFloat(), radius, paint)


//        draw indicator circle
        val markerRadius = radius + RADIUS_OFFSET_INDICATOR
        pointPosition.computeXYForSpeed(fanSpeed, markerRadius)
        paint.color = Color.BLACK
        canvas?.drawCircle(pointPosition.x, pointPosition.y, radius / 12, paint)

//        draw text labels
        val labelRadius = radius + RADIUS_OFFSET_LABEL
        for (i in FanSpeed.values()) {
            pointPosition.computeXYForSpeed(i, labelRadius)
            val label = resources.getString(i.label)
            canvas?.drawText(label, pointPosition.x, pointPosition.y, paint)
        }
    }

}
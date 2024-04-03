package com.sdapps.paddleplay

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class PaddleView(context: Context, attrs: AttributeSet) : View(context, attrs) {


    private val paddlePaint = Paint().apply {
        color = context.getColor(R.color.paddle_color)
    }
    private var paddleRect = RectF()

    var paddleXAxis: Float = 0F
        private set

    private var initialStartXAxis: Float = 0f

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paddleRect.set(
            paddleXAxis,
            (height - PADDLE_HEIGHT - PADDLE_BOTTOM_MARGIN).toFloat(),
            paddleXAxis + PADDLE_WIDTH,
            (height - PADDLE_BOTTOM_MARGIN).toFloat()
        )

        canvas.drawRoundRect(paddleRect, PADDLE_CORNER_RADIUS, PADDLE_CORNER_RADIUS, paddlePaint)

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                initialStartXAxis = event.x
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                val newXPosition = paddleXAxis + (event.x - initialStartXAxis)
                paddleXAxis= newXPosition.coerceIn(0f,width.toFloat() - PADDLE_WIDTH)
                initialStartXAxis = event.x
                invalidate()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    fun setPaddlePosition(x: Float) {
        paddleXAxis = x
    }

    companion object {
        const val PADDLE_WIDTH = 400
        const val PADDLE_HEIGHT = 60
        const val PADDLE_BOTTOM_MARGIN = 20
        const val PADDLE_CORNER_RADIUS = 15f
    }

}
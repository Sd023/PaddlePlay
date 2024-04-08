package com.sdapps.paddleplay.landing

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.random.Random

class StarView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    private val paint = Paint()
    private val stars = mutableListOf<CreateStar>()

    private var animator: ValueAnimator? = null
    private var glowAlpha = 255

    init {
        paint.style = Paint.Style.FILL
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (star in stars) {
            paint.color = star.color
            paint.alpha = glowAlpha
            canvas.withRotation(star.rotation, star.x, star.y) {
                drawPath(star.getPath(), paint)
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        initializeStars(w.toFloat(), h.toFloat())
    }

    private fun initializeStars(viewWidth: Float, viewHeight: Float) {
        val random = Random.Default
        val numStars = MAX_STAR_COUNT
        val paintColors =
            mutableListOf(Color.BLACK, Color.YELLOW, Color.RED, Color.BLUE, Color.GREEN)

        for (i in 0 until numStars) {
            val x = random.nextFloat() * viewWidth
            val y = random.nextFloat() * viewHeight
            val size = random.nextFloat() * (MAX_STAR_SIZE - MIN_STAR_SIZE) + MIN_STAR_SIZE
            val rotation = random.nextFloat() * 360
            val color = paintColors[random.nextInt(paintColors.size)]
            stars.add(CreateStar(x, y, size, rotation, color))
        }
        startRotatingAnimation()
    }

    private fun startRotatingAnimation() {
        val random = Random.Default
        stars.forEach { star ->
            if (random.nextBoolean()) {
                animator = ValueAnimator.ofFloat(0f, 360f).apply {
                    addUpdateListener { valueAnimator ->
                        star.rotation = valueAnimator.animatedValue as Float
                        invalidate()
                    }
                    repeatCount = ValueAnimator.INFINITE
                    duration = randomDuration()
                    start()
                }
            }
        }
    }

    private fun randomDuration(): Long {
        return (Random.nextFloat() * 4000 + 1000).toLong()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animator?.cancel()
    }

    companion object {
        private const val MIN_STAR_SIZE = 20f
        private const val MAX_STAR_SIZE = 80f
        private const val MAX_STAR_COUNT = 100
    }

    private fun Canvas.withRotation(degrees: Float, px: Float, py: Float, block: Canvas.() -> Unit) {
        save()
        rotate(degrees, px, py)
        block()
        restore()
    }
}

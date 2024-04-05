package com.sdapps.paddleplay.landing

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class StarView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    private val paint = Paint()
    private val stars = mutableListOf<Star>()

    private var glowAlpha = 255

    init {
        paint.color = Color.YELLOW
        paint.style = Paint.Style.FILL
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas?.let {
            for (star in stars) {
                paint.alpha = glowAlpha
                it.save()
                it.rotate(star.rotation, star.x, star.y)
                it.drawPath(star.getPath(), paint)
                it.restore()
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

        for (i in 0 until numStars) {
            val x = random.nextFloat() * viewWidth
            val y = random.nextFloat() * viewHeight
            val size = random.nextFloat() * (MAX_STAR_SIZE - MIN_STAR_SIZE) + MIN_STAR_SIZE
            val rotation = random.nextFloat() * 360
            val star = Star(x, y, size, rotation)
            stars.add(star)
        }
        startRotatingAnimation()
        startGlowAnimation()
    }


    private fun startRotatingAnimation() {
        val random = Random.Default
        for (star in stars) {
            if (random.nextBoolean()) {
                val animator = ValueAnimator.ofFloat(0f, 360f)
                animator.addUpdateListener { valueAnimator ->
                    star.rotation = valueAnimator.animatedValue as Float
                    invalidate()
                }
                animator.repeatCount = ValueAnimator.INFINITE
                animator.duration = randomDuration()
                animator.start()
            }
        }
    }

    private fun startGlowAnimation() {
        val random = Random.Default
        for (star in stars) {
            if (random.nextBoolean()) {
                val animator = ValueAnimator.ofInt(150, 255)
                animator.addUpdateListener { valueAnimator ->
                    glowAlpha = valueAnimator.animatedValue as Int
                    invalidate()
                }
                animator.repeatMode = ValueAnimator.REVERSE
                animator.repeatCount = ValueAnimator.INFINITE
                animator.duration = GLOW_ANIMATION_DURATION
                animator.start()
            }
        }
    }


    private fun randomDuration(): Long {
        return (Random.nextFloat() * 4000 + 1000).toLong()
    }


    companion object {
        private const val MIN_STAR_SIZE = 20f
        private const val MAX_STAR_SIZE = 80f
        private const val MAX_STAR_COUNT = 80
        private const val GLOW_ANIMATION_DURATION = 2000L
    }


    /*
    *   Creating Star.
    *
    * */

    data class Star(val x: Float, val y: Float, val size: Float, var rotation: Float) {
        fun getPath(): Path {
            val path = Path()
            val outerRadius = size / 2
            val innerRadius = outerRadius * 0.382f
            val angleOffset = Math.PI / 2

            for (i in 0 until 5) {
                val outerX = (x + outerRadius * cos(angleOffset + i * 2 * Math.PI / 5)).toFloat()
                val outerY = (y + outerRadius * sin(angleOffset + i * 2 * Math.PI / 5)).toFloat()
                val innerX =
                    (x + innerRadius * cos(angleOffset + (i * 2 + 1) * Math.PI / 5)).toFloat()
                val innerY =
                    (y + innerRadius * sin(angleOffset + (i * 2 + 1) * Math.PI / 5)).toFloat()

                if (i == 0) {
                    path.moveTo(outerX, outerY)
                } else {
                    path.lineTo(outerX, outerY)
                }
                path.lineTo(innerX, innerY)
            }
            path.close()
            return path
        }
    }
}
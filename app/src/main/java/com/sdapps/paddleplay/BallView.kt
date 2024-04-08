package com.sdapps.paddleplay

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.media.MediaPlayer
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.AttributeSet
import android.view.View
import kotlin.math.sqrt

class BallView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val ballColor: Paint = Paint().apply {
        color = context.getColor(R.color.black)
    }

    private var ballX: Float = 0f
    private var ballY: Float = 0f
    private val ballRadius: Float = 48f

    private var velocityX: Float = 10f
    private var velocityY: Float = 10f

    var isGameOver: Boolean = false
    private var listenerGame: OnGameOverListener? = null
    private var coolDownTimer: Long = 0
     var paddleHitCount: Int = 0
    private var mediaPlayer: MediaPlayer? = null

    interface OnGameOverListener {
        fun gameOver()
    }

    init {
        ballX = context.resources.displayMetrics.widthPixels / 2f
        ballY = context.resources.displayMetrics.heightPixels / 2f
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawCircle(ballX, ballY, ballRadius, ballColor)
    }

    fun moveBallWithPaddle(paddleX: Float, paddleWidth: Int) {
        ballX += velocityX
        ballY += velocityY

        if (ballX - ballRadius <= 0 || ballX + ballRadius >= width) {
            velocityX = -velocityX
        }

        if (ballY + ballRadius >= height) {
            gameOver()
            resetBall()
            return
        }

        handlePaddleCollision(paddleX, paddleWidth)
        checkCollisionWithTop()
        invalidate()
    }

    private fun handlePaddleCollision(paddleX: Float, paddleWidth: Int) {
        if (checkCollisionWithPaddle(paddleX, paddleWidth)) {
            velocityY = -velocityY
        }
    }

    private fun checkCollisionWithPaddle(paddleX: Float, paddleWidth: Int): Boolean {
        val currentTime = System.currentTimeMillis()
        val timeSinceLastCollision = currentTime - coolDownTimer

        if (timeSinceLastCollision < PADDLE_COLLISION_COOLDOWN) {
            return false
        }
        //purpose of 50 is to add additional height to paddle. So ball wont move through paddle. It hits and bounces back
        val collidesWithPaddle = ballY + ballRadius >= height - (PaddleView.PADDLE_HEIGHT + 50) &&
                ballX >= paddleX && ballX <= paddleX + paddleWidth

        if (collidesWithPaddle) {
            onPaddleCollision()
            coolDownTimer = currentTime
            paddleHitCount++
            return true
        }
        return false
    }

    private fun onPaddleCollision() {
        playSound()
        vibrate()
        increaseSpeed()
    }

    private fun playSound() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, R.raw.ball_collide)
            mediaPlayer?.setVolume(0.5f, 0.5f)
        } else {
            mediaPlayer?.seekTo(0)
        }
        mediaPlayer?.let {
            it.playbackParams = it.playbackParams.setSpeed(1.5f)
            it.start()
        }
    }

    private fun vibrate() {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        vibrator?.let {
            if (it.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    it.vibrate(VibrationEffect.createOneShot(50, 1))
                } else {
                    it.vibrate(50)
                }
            }
        }
    }

    private fun increaseSpeed() {
        val newSpeedX = velocityX + BALL_SPEED_INCREMENT
        val newSpeedY = velocityY + BALL_SPEED_INCREMENT

        velocityX = if (newSpeedX > MAX_BALL_SPEED) MAX_BALL_SPEED else newSpeedX
        velocityY = if (newSpeedY > MAX_BALL_SPEED) MAX_BALL_SPEED else newSpeedY


        val speed = sqrt((velocityX * velocityX + velocityY * velocityY).toDouble())
        if (speed > MAX_BALL_SPEED) {
            val factor = MAX_BALL_SPEED / speed
            velocityX *= factor.toFloat()
            velocityY *= factor.toFloat()
        }
    }

    private fun checkCollisionWithTop() {
        if (ballY - ballRadius <= 0) {
            velocityY = -velocityY
            ballY = ballRadius + 1
        }
    }

    fun stopGame() {
        velocityY = 0f
        velocityX = 0f
        mediaPlayer?.release()
        mediaPlayer = null
    }

    fun restartGame() {
        paddleHitCount = 0
        velocityX = 10f
        velocityY = 10f
    }

    private fun gameOver() {
        isGameOver = true
        listenerGame?.gameOver()
    }

    fun resetBall() {
        ballX = context.resources.displayMetrics.widthPixels / 2f
        ballY = context.resources.displayMetrics.heightPixels / 2f
        invalidate()
    }

    fun setUpGameOver(listener: OnGameOverListener) {
        listenerGame = listener
    }

    companion object {
        private const val PADDLE_COLLISION_COOLDOWN = 500L
        private const val BALL_SPEED_INCREMENT = 10
        private const val MAX_BALL_SPEED = 50f
    }
}

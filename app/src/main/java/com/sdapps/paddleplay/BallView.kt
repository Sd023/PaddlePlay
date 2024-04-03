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

class BallView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val ballColor = Paint().apply {
        color = context.getColor(R.color.ball_color)
    }

    /*   X and Y axis of Ball */
    private var ballY: Float = 0f
    private var ballX: Float = 0f
    private var ballRadius: Float = 48f


    /*    Speed of the ball  */
    private var velocityX: Float
    private var velocityY: Float
    var isGameOver: Boolean = false

    private var listenerGame : OnGameOverListener? = null

    private var coolDownTimer :Long = 0
    var paddleHitCount : Int = 0

    private var mediaPlayer: MediaPlayer? = null

    interface OnGameOverListener{
        fun gameOver()
    }

    fun setUpGameOver(listener: OnGameOverListener){
        listenerGame = listener
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawCircle(ballX, ballY, ballRadius, ballColor)
    }

    init {
        ballX = context.resources.displayMetrics.widthPixels / 2f
        ballY = context.resources.displayMetrics.heightPixels / 2f

        velocityX = 20f
        velocityY = 20f
    }


    fun moveBallWithPaddle(paddleX: Float, paddleWidth: Int) {
        ballX += velocityX
        ballY += velocityY

        //checking if ball hits any edge of the screen.
        if (ballX - ballRadius <= 0 || ballX + ballRadius >= width) {
            velocityX = -velocityX
        }

        /*checking if ball drops below the paddle*/
        if (ballY + ballRadius >= height) {
            isGameOver = true
            listenerGame?.gameOver()
            resetBall()
        }
        handlePaddleCollision(paddleX, paddleWidth)
        checkCollisionWithTop()
        invalidate()
    }


    fun checkCollisionWithPaddle(paddleX: Float, paddleWidth: Int, paddleHeight: Int): Boolean {
        val currentTime = System.currentTimeMillis()
        val timeSinceLastCollision = currentTime - coolDownTimer

        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator


        if (timeSinceLastCollision < PADDLE_COLLISION_COOLDOWN) {
            return false
        }
        val collidesWithPaddle = ballY + ballRadius >= height - paddleHeight && ballX >= paddleX && ballX <= paddleX + paddleWidth

        if (collidesWithPaddle) {
            playSound()
            if(vibrator.hasVibrator()){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(50, 1))
                } else {
                    vibrator.vibrate(50)
                }
            }
            coolDownTimer = currentTime
            paddleHitCount++

            increaseSpeed()
            return true
        }
        return false
    }

    private fun handlePaddleCollision(paddleX: Float, paddleWidth: Int) {
        if (checkCollisionWithPaddle(paddleX, paddleWidth, PaddleView.PADDLE_HEIGHT)) {
            velocityY = -velocityY
        }
    }

    private fun checkCollisionWithTop() {
        if (ballY - ballRadius <= 0) {
            velocityY = -velocityY
            ballY = ballRadius + 1
        }
    }

    private fun playSound(){
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


    fun stopGame() {
        velocityY = 0f
        velocityX = 0f
        mediaPlayer?.release()
        mediaPlayer = null
    }

    fun restartGame(){
        paddleHitCount = 0
        velocityX = 20f
        velocityY = 20f
    }

    private fun increaseSpeed() {
        val speedMap = mapOf(
            3 to 25f,
            5 to 32f,
            10 to 42f,
            15 to 48f,
            20 to 55f,
            22 to 62f
        )

        val maxHitCount = speedMap.keys.filter { it <= paddleHitCount }.maxOrNull()

        if (maxHitCount != null) {
            val velocity = speedMap[maxHitCount]
            velocityX = velocity!!
            velocityY = velocity
        }
    }


    fun resetBall() {
        ballX = context.resources.displayMetrics.widthPixels / 2f
        ballY = context.resources.displayMetrics.heightPixels / 2f
        invalidate()
    }

    companion object {
        private const val PADDLE_COLLISION_COOLDOWN = 500L
        private const val BALL_SPEED_INCREASE_STEP = 5

    }
}
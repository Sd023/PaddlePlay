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
    private var velocityX: Float = 5f
    private var velocityY: Float = 5f
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

        velocityX = 10f
        velocityY = 10f
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
            if(paddleHitCount > BALL_SPEED_INCREASE_STEP){
                increaseSpeed()
            }
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
        }
    }

    private fun playSound(){
        mediaPlayer = MediaPlayer.create(context, R.raw.ball_collide)
        mediaPlayer?.let {
            it.playbackParams = it.playbackParams.setSpeed(1.5f)
            it.start()
        }
        mediaPlayer?.setVolume(0.5f, 0.5f)

    }


    fun stopGame() {
        velocityY = 0f
        velocityX = 0f
        mediaPlayer?.release()
        mediaPlayer = null
    }

    fun restartGame(){
        paddleHitCount = 0
        velocityX = 10f
        velocityY = 10f
    }

    private fun increaseSpeed(){
        if((velocityX < 20f) && (velocityY < 20f)){
            velocityY += 2f
            velocityX += 2f
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
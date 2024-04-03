package com.sdapps.paddleplay

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class BallView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val ballColor = Paint().apply {
        color = Color.BLACK
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

        if (timeSinceLastCollision < PADDLE_COLLISION_COOLDOWN) {
            return false
        }
        val collidesWithPaddle = ballY + ballRadius >= height - paddleHeight && ballX >= paddleX && ballX <= paddleX + paddleWidth

        if (collidesWithPaddle) {
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

    fun stopGame() {
        velocityY = 0f
        velocityX = 0f
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
package com.sdapps.paddleplay

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.sdapps.paddleplay.databinding.ActivityMainBinding
import com.sdapps.paddleplay.PaddleView.Companion.PADDLE_WIDTH
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity(), BallView.OnGameOverListener {
    private lateinit var binding: ActivityMainBinding
    private var handler: Handler = Handler()
    private lateinit var runnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.apply {
            decorView.systemUiVisibility = (
                    android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            or android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
            statusBarColor = android.graphics.Color.TRANSPARENT
        }
        binding.ball.setUpGameOver(this)
        init()
    }


    private fun init() {
        val initialPaddleX = (binding.root.width - PADDLE_WIDTH) / 2f
        binding.paddle.setPaddlePosition(initialPaddleX)
        binding.ball.resetBall()
        startGameLoop()
    }


    private fun handleCollisions() {
        updatePaddleHitCount(binding.ball.paddleHitCount)
    }

    private fun startGameLoop() {
        val frameRate = 16
        runnable = object : Runnable {
            override fun run() {
                binding.ball.moveBallWithPaddle(binding.paddle.paddleXAxis,PADDLE_WIDTH)
                handleCollisions()
                handler.postDelayed(this, frameRate.toLong())
            }
        }
        handler.post(runnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)

    }

    override fun gameOver() {
        binding.ball.stopGame()
        binding.ball.resetBall()
        showRestartDialog()
        updatePaddleHitCount(0)
    }

    private fun showRestartDialog(){
        AlertDialog.Builder(this)
            .setTitle(R.string.game_over)
            .setMessage("${getString(R.string.high_score)} is ${binding.ball.paddleHitCount}. ${getString(R.string.do_you_want_to_restart)}")
            .setCancelable(false)
            .setPositiveButton(R.string.restart) { dialog, _ ->
                //count = 0
                //binding.counter.text = count.toString()
                restartGame()
                dialog.dismiss()
            }
            .setNegativeButton(R.string.quit) { dialog, _ ->
                restartGame()
                dialog.dismiss()
            }
            .show()
    }

    private fun restartGame() {
        binding.ball.restartGame()
        binding.paddle.setPaddlePosition((resources.displayMetrics.widthPixels - PADDLE_WIDTH) / 2f)
        binding.ball.isGameOver = false
    }

    private fun updatePaddleHitCount(hitCount: Int) {
        binding.counter.text = hitCount.toString()
    }
}
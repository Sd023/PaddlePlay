package com.sdapps.paddleplay.landing

import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.view.animation.TranslateAnimation
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.sdapps.paddleplay.MainActivity
import com.sdapps.paddleplay.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class LandingPage : AppCompatActivity() {

    private lateinit var landingBall: View
    private lateinit var startGame: Button

    private lateinit var displayMetrics: DisplayMetrics
    private var screenWidth: Double = 900.0
    private val durationInMillis = 3000
    private val duration = 1500L
    private val distance = 1000f


    private lateinit var starView: StarView
    private var coroutineJob: Job? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing_page)
        displayMetrics = DisplayMetrics()
        (windowManager.defaultDisplay).getMetrics(displayMetrics)
        screenWidth = displayMetrics.widthPixels * 0.80
        starView = findViewById(R.id.starView)

        init()
    }


    private fun init() {
        landingBall = findViewById(R.id.landingBall)
        startGame = findViewById(R.id.startGame)
        startAnimationForBat()
        startBallAnim()
        startGame.setOnClickListener {
            startActivity(Intent(this@LandingPage, MainActivity::class.java))
        }

    }

    private fun startAnimationForBat() {

        val leftBat = findViewById<View>(R.id.leftBat)
        val rightBat = findViewById<View>(R.id.rightBat)

        val leftAnimator = ObjectAnimator.ofFloat(leftBat, "translationY", 0f, distance)
        leftAnimator.repeatMode = ObjectAnimator.REVERSE
        leftAnimator.repeatCount = ObjectAnimator.INFINITE
        leftAnimator.duration = duration
        leftAnimator.start()

        val rightAnimator = ObjectAnimator.ofFloat(rightBat, "translationY", 0f, -distance)
        rightAnimator.repeatMode = ObjectAnimator.REVERSE
        rightAnimator.repeatCount = ObjectAnimator.INFINITE
        rightAnimator.duration = duration
        rightAnimator.start()
    }

    private fun startBallAnim() {

        val animation = TranslateAnimation(
            0f, screenWidth.toFloat(),
            0f, screenWidth.toFloat()
        )
        animation.duration = durationInMillis.toLong()
        animation.interpolator = LinearInterpolator()

        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}

            override fun onAnimationEnd(animation: Animation) {
                startReverse()
            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })
        landingBall.startAnimation(animation)
    }

    fun startReverse() {
        val reverseAnimation = TranslateAnimation(
            screenWidth.toFloat(), 0f,
            screenWidth.toFloat(), 0f
        )
        reverseAnimation.duration = durationInMillis.toLong()
        reverseAnimation.fillAfter = true
        reverseAnimation.interpolator = LinearInterpolator()

        reverseAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                startBallAnim()
            }

            override fun onAnimationRepeat(animation: Animation?) {

            }
        })
        landingBall.startAnimation(reverseAnimation)
    }

    override fun onDestroy() {
        coroutineJob?.cancel()
        super.onDestroy()
    }
}
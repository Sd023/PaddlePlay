package com.sdapps.paddleplay.landing

import android.graphics.Path
import kotlin.math.cos
import kotlin.math.sin

data class CreateStar(val x: Float, val y: Float, val size: Float, var rotation: Float, var color: Int) {
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
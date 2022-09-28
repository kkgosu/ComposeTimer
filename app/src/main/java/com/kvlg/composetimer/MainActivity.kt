package com.kvlg.composetimer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import com.kvlg.composetimer.ui.theme.ComposeTimerTheme
import com.kvlg.composetimer.ui.theme.Typography
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeTimerTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Timer()
                }
            }
        }
    }
}

val BgColorCenter = Color(0xFF404364)
val BgColorEdge = Color(0xFF2E3048)
val DarkRed = Color(0xFFD53175)
val LightOrange = Color(0xFFF7886A)

@Composable
@Preview
fun Countdown() {
    TimerCircleGesturesTheme {
        Column(modifier = Modifier
            .fillMaxSize()
            .background(Brush.radialGradient(listOf(BgColorCenter, BgColorEdge))),
            verticalArrangement = Arrangement.Center) {
            Box(modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)) {
                for (i in 0 until 72) {
                    TickMark(angle = i * 5, on = i < 15)
                }
            }
        }
    }
}

val Offset.theta get() = (atan2(y.toDouble(), x.toDouble()) * 180.0 / PI).toFloat()
const val StartRadiusFraction = 0.6f
const val EndRadiusFraction = 0.75f
const val TickWidth = 9f

@Composable
fun TickMark(
    angle: Int,
    on: Boolean,
) {
    Box(modifier = Modifier
        .fillMaxSize()
        .drawBehind {
            val theta = angle * PI.toFloat() / 180f
            val startRadius = size.width / 2 * StartRadiusFraction
            val endRadius = size.width / 2 * EndRadiusFraction
            val startPos = Offset(
                cos(theta) * startRadius,
                sin(theta) * startRadius
            )
            val endPos = Offset(
                cos(theta) * endRadius,
                sin(theta) * endRadius
            )
            drawLine(color = if (on) DarkRed else Color.White.copy(alpha = 0.2f),
                start = center + startPos,
                end = center + endPos,
                strokeWidth = TickWidth,
                cap = StrokeCap.Round)
        })
}

@Composable
fun TimerCircleGesturesTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(primary = DarkRed, secondary = LightOrange, background = BgColorEdge),
        typography = Typography
    ) {
        content()
    }
}
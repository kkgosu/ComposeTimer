package com.kvlg.composetimer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.center
import androidx.compose.ui.unit.toOffset
import com.kvlg.composetimer.ui.theme.ComposeTimerTheme
import com.kvlg.composetimer.ui.theme.Typography
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
    val scope = rememberCoroutineScope()
    val state by remember { mutableStateOf(TickWheelState(scope)) }
    TimerCircleGesturesTheme {
        Column(modifier = Modifier
            .fillMaxSize()
            .background(Brush.radialGradient(listOf(BgColorCenter, BgColorEdge))),
            verticalArrangement = Arrangement.Center) {
            TickWheel(modifier = Modifier.fillMaxWidth(), state = state, ticks = 40, startColor = LightOrange, endColor = DarkRed) {

            }
        }
    }
}

class TickWheelState(
    private val scope: CoroutineScope,
) {
    var totalSeconds by mutableStateOf(0)
        private set
    var isDragging by mutableStateOf(false)
    var position by mutableStateOf<Offset?>(null)
    var secondsLeft by mutableStateOf<Int>(0)
    private var endPosition by mutableStateOf<Offset?>(null)
    private var job: Job? = null

    fun startDrag(startPos: Offset) {
        position = startPos
        isDragging = true
        stop()
    }

    fun onDrag(delta: Offset) {
        val current = position
        val next = current?.let { it + delta } ?: delta
        secondsLeft = ((next.theta + 180f) / 360 * 60).roundToInt()
        position = next
    }

    fun endDrag() {
        val current = position
        current?.let {
            secondsLeft = ((it.theta + 180f) / 360 * 60).roundToInt()
            isDragging = false
        } ?: run {
            error("Position was null when it shouldn't have been")
        }
    }

    fun stop() {
        job?.cancel()
        job = null
    }

    fun toggle() {
        if (job == null) {
            job = scope.launch {
                while (totalSeconds > 0) {
                    delay(1000)
                    countDown()
                }
                endPosition = null
            }
        } else {
            stop()
        }
    }

    private fun countDown() {
        val next = totalSeconds - 1
        val theta = (((next % 60) * 6 - 180) * PI / 180).toFloat()
        val radius = 100f
        totalSeconds = next
        endPosition = Offset(
            cos(theta) * radius,
            sin(theta) * radius
        )
    }
}

@Composable
fun TickWheel(
    modifier: Modifier,
    ticks: Int,
    startColor: Color,
    endColor: Color,
    state: TickWheelState,
    content: @Composable () -> Unit,
) {
    var origin by remember { mutableStateOf(Offset.Zero) }
    Box(modifier = modifier
        .onSizeChanged { origin = it.center.toOffset() }
        .aspectRatio(1f)
        .pointerInput(Unit) {
            detectDragGestures(
                onDragStart = { state.startDrag(it - origin) },
                onDragEnd = { state.endDrag() },
                onDragCancel = { state.endDrag() },
                onDrag = { change, dragAmount ->
                    state.onDrag(dragAmount)
                    change.consume()
                }
            )
        }
        .drawBehind {
            val endTheta = state.position?.theta ?: -180f
            val startRadius = size.width / 2 * StartRadiusFraction
            val endRadius = size.width / 2 * EndRadiusFraction
            val sweep = Brush.sweepGradient(
                0f to startColor,
                1f to endColor
            )
            val offBrush = SolidColor(Color.White.copy(alpha = 0.1f))
            for (i in 0 until ticks) {
                val angle = i * (360 / ticks) - 180
                val theta = angle * PI.toFloat() / 180f
                val on = angle < endTheta
                val startPos = Offset(
                    cos(theta) * startRadius,
                    sin(theta) * startRadius
                )
                val endPos = Offset(
                    cos(theta) * endRadius,
                    sin(theta) * endRadius
                )
                drawLine(brush = if (on) sweep else offBrush,
                    start = center + startPos,
                    end = center + endPos,
                    strokeWidth = TickWidth,
                    cap = StrokeCap.Round)
            }
        }) {
        content()
    }
}

val Offset.theta get() = (atan2(y.toDouble(), x.toDouble()) * 180.0 / PI).toFloat()
const val StartRadiusFraction = 0.5f
const val EndRadiusFraction = 0.75f
const val TickWidth = 9f

@Composable
fun TimerCircleGesturesTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(primary = DarkRed, secondary = LightOrange, background = BgColorEdge),
        typography = Typography
    ) {
        content()
    }
}
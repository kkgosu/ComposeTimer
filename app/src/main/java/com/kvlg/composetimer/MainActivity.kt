package com.kvlg.composetimer

import android.graphics.Rect
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kvlg.composetimer.ui.theme.ComposeTimerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeTimerTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Composable
@Preview
fun Timer() {
    val angle = remember { Animatable(InitialValue) }
    val borderWidth = with(LocalDensity.current) { 12.dp.toPx() }
    val textColor = MaterialTheme.colorScheme.onBackground
    val textPaint = Paint().asFrameworkPaint().apply {
        color = textColor.toArgb()
        textSize = 100.sp.value
    }
    var timerValue1 by remember { mutableStateOf(100) }
    var timerValue2 by remember { mutableStateOf(100) }
    val text = "$timerValue1 $timerValue2"
    val textRect = Rect()
    textPaint.getTextBounds(text, 0, text.length, textRect)
    LaunchedEffect(key1 = Unit) {
        angle.animateTo(
            targetValue = TargetValue,
            animationSpec = tween(durationMillis = 5000, easing = LinearEasing)
        )
    }
    val background = MaterialTheme.colorScheme.primary
    val borderColor = MaterialTheme.colorScheme.secondary
    var isLogged = true
    Canvas(modifier = Modifier
        .padding(16.dp)
        .size(256.dp)
        .pointerInput(key1 = Unit) {
            var isInRightPart = false
            detectDragGesturesAfterLongPress(
                onDragStart = {
                    isInRightPart = it.x >= size.width / 2
                },
                onDrag = { input, offset ->
                    if (isInRightPart) {
                        timerValue2 -= offset.y.toInt()
                    } else {
                        timerValue1 -= offset.y.toInt()
                    }
                }
            )
        }) {
        drawCircle(color = background)
        drawArc(color = borderColor,
            startAngle = StartAngle,
            sweepAngle = angle.value,
            useCenter = false,
            style = Stroke(width = borderWidth, cap = StrokeCap.Round))
        drawContext.canvas.nativeCanvas.drawText(
            text,
            size.width / 2 - textRect.width() / 2,
            size.height / 2 + textRect.height() / 2,
            textPaint
        )
    }
}

private const val InitialValue = 360f
private const val TargetValue = 0f
private const val StartAngle = -90f
private const val OneSecond = 1000L
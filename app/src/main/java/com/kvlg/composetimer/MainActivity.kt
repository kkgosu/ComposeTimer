package com.kvlg.composetimer

import android.graphics.Rect
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
                }
            }
        }
    }
}

@Composable
@Preview
fun Timer() {
    Box(modifier = Modifier.fillMaxSize()) {
        val angle = remember { Animatable(InitialValue) }
        var isTutorialVisible by remember { mutableStateOf(false) }
        var isTimerStarted by remember { mutableStateOf(false) }
        val borderWidth = with(LocalDensity.current) { 12.dp.toPx() }
        val textColor = Color.White
        val textPaint = remember {
            Paint().asFrameworkPaint().apply {
                color = textColor.toArgb()
                textSize = 120.sp.value
            }
        }
        var timerValue1 by remember { mutableStateOf(100) }
        var timerValue2 by remember { mutableStateOf(100) }
        val text = "$timerValue1:$timerValue2"
        val textRect = Rect()
        textPaint.getTextBounds(text, 0, text.length, textRect)
        LaunchedEffect(key1 = isTimerStarted) {
            if (isTimerStarted) {
                angle.animateTo(targetValue = TargetValue, animationSpec = tween(durationMillis = 5000, easing = LinearEasing))
            }
        }
        val background = MaterialTheme.colorScheme.primary
        val borderColor = MaterialTheme.colorScheme.secondary
        Canvas(modifier = Modifier
            .align(Alignment.Center)
            .padding(16.dp)
            .size(256.dp)
            .pointerInput(key1 = Unit) {
                var isInRightPart = false
                detectDragGesturesAfterLongPress(
                    onDragStart = {
                        isTutorialVisible = true
                        isInRightPart = it.x >= size.width / 2
                    },
                    onDrag = { input, offset ->
                        if (isInRightPart) {
                            timerValue2 -= offset.y.toInt()
                        } else {
                            timerValue1 -= offset.y.toInt()
                        }
                    },
                    onDragEnd = {
                        isTutorialVisible = false
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
        IconButton(onClick = { isTimerStarted = !isTimerStarted }, modifier = Modifier
            .align(Alignment.BottomStart)
            .size(120.dp)) {
            if (isTimerStarted) {
                Icon(imageVector = Icons.Rounded.ArrowBack,
                    tint = Color.Green,
                    contentDescription = "Pause timer", modifier = Modifier.size(120.dp))
            } else {
                Icon(imageVector = Icons.Rounded.PlayArrow,
                    tint = Color.Green,
                    contentDescription = "Star timer", modifier = Modifier.size(120.dp))
            }
        }
        IconButton(onClick = { }, modifier = Modifier
            .align(Alignment.BottomEnd)
            .size(120.dp)) {
            Icon(imageVector = Icons.Rounded.Refresh,
                tint = Color.Black,
                contentDescription = "Reset timer", modifier = Modifier.size(120.dp))
        }
        AnimatedVisibility(visible = isTutorialVisible, enter = fadeIn(), exit = fadeOut()) {
            Box(modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Black.copy(alpha = 0.7f))
                .padding(16.dp)) {
                Text(text = "Minutes", modifier = Modifier.align(Alignment.BottomStart))
                Text(text = "Seconds", modifier = Modifier.align(Alignment.BottomEnd))
            }
        }
    }
}

private const val InitialValue = 360f
private const val TargetValue = 0f
private const val StartAngle = -90f
private const val OneSecond = 1000L
package com.kvlg.composetimer

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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
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
    var timerValue1 by remember { mutableStateOf(100) }
    var timerValue2 by remember { mutableStateOf(100) }
    var isTutorialVisible by remember { mutableStateOf(false) }
    val angle = remember { Animatable(InitialValue) }
    var isTimerStarted by remember { mutableStateOf(false) }
    val borderWidth = with(LocalDensity.current) { 12.dp.toPx() }
    var isInLeftSide by remember { mutableStateOf(false) }
    LaunchedEffect(key1 = isTimerStarted) {
        if (isTimerStarted) {
            angle.animateTo(targetValue = TargetValue, animationSpec = tween(durationMillis = 5000, easing = LinearEasing))
        }
    }
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        Box(modifier = Modifier.align(Alignment.Center)) {
            val background = MaterialTheme.colorScheme.primary
            val borderColor = MaterialTheme.colorScheme.secondary
            Canvas(modifier = Modifier
                .align(Alignment.Center)
                .padding(16.dp)
                .size(256.dp)
                .pointerInput(Unit) {
                    detectDragGesturesAfterLongPress(
                        onDragStart = {
                            isTutorialVisible = true
                            isInLeftSide = it.x <= size.width / 2
                        },
                        onDrag = { input, offset ->
                            if (isInLeftSide) {
                                timerValue1 -= offset.y.toInt()
                            } else {
                                timerValue2 -= offset.y.toInt()
                            }
                        },
                        onDragEnd = {
                            isTutorialVisible = false
                        },
                        onDragCancel = {
                            isTutorialVisible = false
                        }
                    )
                }
            ) {
                drawCircle(color = background)
                drawArc(color = borderColor,
                    startAngle = StartAngle,
                    sweepAngle = angle.value,
                    useCenter = false,
                    style = Stroke(width = borderWidth, cap = StrokeCap.Round))
            }
            TimerText("$timerValue1", "$timerValue2", isTutorial = false)
        }
        if (isTimerStarted) {
            BottomButton(icon = Icons.Rounded.ArrowBack, onClick = { isTimerStarted = !isTimerStarted }, isLeftButton = true)
        } else {
            BottomButton(icon = Icons.Rounded.PlayArrow, onClick = { isTimerStarted = !isTimerStarted }, isLeftButton = true)
        }
        BottomButton(icon = Icons.Rounded.Refresh, onClick = { isTimerStarted = !isTimerStarted }, isLeftButton = false)
    }
    AnimatedVisibility(visible = isTutorialVisible, enter = fadeIn(), exit = fadeOut()) {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black.copy(alpha = 0.7f))
            .padding(16.dp)) {
            TimerText("$timerValue1", "$timerValue2", isTutorial = true, isLeftSide = isInLeftSide)
            Text(text = "Minutes", modifier = Modifier.align(Alignment.BottomStart))
            Text(text = "Seconds", modifier = Modifier.align(Alignment.BottomEnd))
        }
    }
}

@Composable
fun BoxScope.BottomButton(
    icon: ImageVector,
    onClick: () -> Unit,
    isLeftButton: Boolean,
) {
    val indication = rememberRipple(
        bounded = false,
        radius = 50.dp
    )
    val alignment = if (isLeftButton) Alignment.BottomStart else Alignment.BottomEnd
    Icon(imageVector = icon,
        tint = Color.Green,
        contentDescription = "", modifier = Modifier
            .padding(start = 32.dp, end = 32.dp)
            .size(100.dp)
            .align(alignment)
            .clickable(interactionSource = remember { MutableInteractionSource() },
                indication = indication,
                onClick = onClick
            )
            .clip(CircleShape)
            .background(Color.LightGray)
    )
}

@Composable
private fun BoxScope.TimerText(
    value1: String,
    value2: String,
    isTutorial: Boolean = false,
    isLeftSide: Boolean = true,
) {
    val leftTextColor = if (isTutorial && !isLeftSide) Color.Transparent else Color.White
    val rightTextColor = if (isTutorial && isLeftSide) Color.Transparent else Color.White
    val colonTextColor = if (isTutorial) Color.Transparent else Color.White
    Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.align(Alignment.Center)) {
        Text(text = value1, color = leftTextColor, fontSize = 60.sp)
        Text(text = ":", color = colonTextColor, fontSize = 60.sp)
        Text(text = value2, color = rightTextColor, fontSize = 60.sp)
    }
}

private const val InitialValue = 360f
private const val TargetValue = 0f
private const val StartAngle = -90f
private const val OneSecond = 1000L
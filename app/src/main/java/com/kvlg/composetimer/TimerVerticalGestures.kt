package com.kvlg.composetimer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

/**
 * @author Konstantin Koval on 25.09.2022
 */
@Composable
@Preview
fun Timer() {
    var timerValue1 by remember { mutableStateOf(10) }
    var timerValue2 by remember { mutableStateOf(10) }
    var isTutorialVisible by remember { mutableStateOf(false) }
    val angle = remember { Animatable(InitialValue) }
    var isTimerStarted by remember { mutableStateOf(false) }
    var isInLeftSide by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    LaunchedEffect(key1 = isTimerStarted) {
        if (isTimerStarted) {
            angle.animateTo(targetValue = TargetValue, animationSpec = tween(durationMillis = 5000, easing = LinearEasing))
        }
    }
    Box(modifier = Modifier
        .fillMaxSize()
        .background(color = Color(0xFF77A18A))
        .padding(32.dp)) {
        Text(modifier = Modifier
            .align(Alignment.TopCenter)
            .padding(top = 4.dp),
            text = "Compose Timer",
            color = Color.Black.copy(alpha = 0.2f),
            fontSize = 40.sp,
            fontStyle = FontStyle.Italic)
        Text(modifier = Modifier.align(Alignment.TopCenter),
            text = "Compose Timer",
            color = Color.White,
            fontSize = 40.sp,
            fontStyle = FontStyle.Italic)
        Box(modifier = Modifier.align(Alignment.Center)) {
            val background = Color(0xFF60816F)
            val borderColor = Color.White
            Canvas(modifier = Modifier
                .align(Alignment.Center)
                .padding(16.dp)
                .size(256.dp)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            isTutorialVisible = true
                            isInLeftSide = it.x <= size.width / 2
                            scope.launch { isTutorialVisible = !tryAwaitRelease() }
                        }
                    )
                }
                .pointerInput(Unit) {
                    detectVerticalDragGestures(
                        onVerticalDrag = { change, dragAmount ->
                            isTutorialVisible = true
                            isInLeftSide = change.position.x <= size.width / 2
                            if (isInLeftSide) {
                                val temp = timerValue1 - (dragAmount).toInt()
                                timerValue1 = temp.coerceAtLeast(0)
                            } else {
                                val temp = timerValue2 - (dragAmount).toInt()
                                timerValue2 = temp.coerceIn(0..599)
                            }
                        },
                        onDragStart = {
                            isTutorialVisible = true
                        },
                        onDragCancel = {
                            isTutorialVisible = false
                        },
                        onDragEnd = {
                            isTutorialVisible = false
                        }
                    )
                }
            ) {
                drawCircle(color = background)
                drawArc(color = Color.Black,
                    startAngle = StartAngle,
                    sweepAngle = angle.value,
                    useCenter = false,
                    alpha = 0.2f,
                    size = size,
                    topLeft = Offset(0f, 20f),
                    style = Stroke(width = size.minDimension * 0.05f, cap = StrokeCap.Round))
                drawArc(color = borderColor,
                    startAngle = StartAngle,
                    sweepAngle = angle.value,
                    useCenter = false,
                    size = size,
                    style = Stroke(width = size.minDimension * 0.05f, cap = StrokeCap.Round))
            }
            TimerText("${timerValue1 / 10}", "${timerValue2 / 10}", isTutorial = false)
        }
        Row(modifier = Modifier.align(Alignment.BottomCenter), horizontalArrangement = Arrangement.Center) {
            if (isTimerStarted) {
                BottomButton(icon = Icons.Rounded.ArrowBack, onClick = { isTimerStarted = !isTimerStarted }, isLeftButton = true)
            } else {
                BottomButton(icon = Icons.Rounded.PlayArrow, onClick = { isTimerStarted = !isTimerStarted }, isLeftButton = true)
            }
            BottomButton(icon = Icons.Rounded.Refresh,
                onClick = { timerValue1 = 0; timerValue2 = 0; scope.launch { angle.snapTo(InitialValue) }; isTimerStarted = false },
                isLeftButton = false)
        }
    }
    AnimatedVisibility(visible = isTutorialVisible, enter = fadeIn(), exit = fadeOut()) {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black.copy(alpha = 0.7f))
            .padding(16.dp)) {
            TimerText("${timerValue1 / 10}", "${timerValue2 / 10}", isTutorial = true, isLeftSide = isInLeftSide)
            Text(text = "Minutes", modifier = Modifier.align(Alignment.BottomStart))
            Text(text = "Seconds", modifier = Modifier.align(Alignment.BottomEnd))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomButton(
    icon: ImageVector,
    onClick: () -> Unit,
    isLeftButton: Boolean,
    isActive: Boolean = true,
) {
    val alpha = if (isActive) 0.95f else 0.5f
    val color = if (isLeftButton) Color.White.copy(alpha = alpha) else Color.Black.copy(alpha = 0.25f)
    val tint = if (isLeftButton) Color(0xFF4BB583) else Color.White
    val size = 80.dp
    Surface(modifier = Modifier
        .padding(start = 32.dp, end = 32.dp),
        shape = CircleShape, color = color, onClick = onClick, enabled = !isLeftButton || (isLeftButton && isActive)) {
        Icon(imageVector = icon,
            tint = tint,
            contentDescription = "", modifier = Modifier
                .padding(8.dp)
                .size(size)
                .clip(CircleShape)
        )
    }
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
    Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.align(Alignment.Center)) {
        Text(text = value1, color = leftTextColor, fontSize = 60.sp, fontStyle = FontStyle.Italic)
        Text(text = ":", color = Color.White, fontSize = 60.sp, fontStyle = FontStyle.Italic)
        Text(text = value2, color = rightTextColor, fontSize = 60.sp, fontStyle = FontStyle.Italic)
    }
}

private const val InitialValue = 360f
private const val TargetValue = 0f
private const val StartAngle = -90f
private const val OneSecond = 1000L
package com.example.myapplication01

import android.graphics.Matrix
import android.graphics.SweepGradient
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.BasicText
import kotlinx.coroutines.delay

/**
 * UI 流光引擎：負責生成 128 色流光陣列與動態繪製組件
 */
object RainbowEngine {
    val colors128 = List(128) { i ->
        Color.hsv(i * 360f / 128f, 1f, 1f)
    }
    val colorInts = colors128.map { it.toArgb() }.toIntArray()

    @Composable
    fun getSharedRotation(): Float {
        val rotationState = produceState(initialValue = 0f) {
            while (true) {
                withFrameMillis { ms ->
                    value = (ms % 4000) / 4000f * 360f
                }
            }
        }
        return rotationState.value
    }

    @Composable
    fun getSharedTime(): Long {
        val timeState = produceState(initialValue = 0L) {
            while (true) {
                withFrameMillis { ms ->
                    value = ms
                }
            }
        }
        return timeState.value
    }
}

/**
 * LightAppSuiteBox：外層螢幕邊框流光
 */
@Composable
fun LightAppSuiteBox(
    modifier: Modifier = Modifier
) {
    val isEnabled = GlobalState.isRainbowEnabled
    if (!isEnabled) return

    val rotation = RainbowEngine.getSharedRotation()
    val animatedBrush = remember(rotation) {
        object : ShaderBrush() {
            override fun createShader(size: Size): Shader {
                val shader = SweepGradient(size.center.x, size.center.y, RainbowEngine.colorInts, null)
                val matrix = Matrix()
                matrix.postRotate(rotation, size.center.x, size.center.y)
                shader.setLocalMatrix(matrix)
                return shader
            }
        }
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(start = 14.dp, end = 14.dp, bottom = 28.dp, top = 2.dp)
    ) {
        drawRoundRect(
            brush = animatedBrush,
            style = Stroke(width = 3.5.dp.toPx()), // 稍微加粗邊框以增加層次
            cornerRadius = CornerRadius(24.dp.toPx())
        )
    }
}

/**
 * LightAppFullscreenAtmosphere：全螢幕 128 色磨砂旋轉呼吸背景
 */
@Composable
fun LightAppFullscreenAtmosphere() {
    val isEnabled = GlobalState.isRainbowEnabled
    if (!isEnabled) return

    // 1. 全螢幕脈衝呼吸動畫 (Alpha 脈衝)
    val infiniteTransition = rememberInfiniteTransition(label = "AtmospherePulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.45f,
        animationSpec = infiniteRepeatable(
            animation = tween(3500, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "AlphaPulse"
    )

    // 2. 中心點旋轉 128 色磨砂漸變
    val rotation = RainbowEngine.getSharedRotation()
    val atmosphereBrush = remember(rotation) {
        object : ShaderBrush() {
            override fun createShader(size: Size): Shader {
                // 以畫面正中心為軸心
                val shader = SweepGradient(size.center.x, size.center.y, RainbowEngine.colorInts, null)
                val matrix = Matrix()
                matrix.postRotate(rotation, size.center.x, size.center.y)
                shader.setLocalMatrix(matrix)
                return shader
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            // 3. 剪裁邊界：將光芒限制在 128 色外框內部 (與外框 Padding 對齊)
            .padding(start = 14.dp, end = 14.dp, bottom = 28.dp, top = 2.dp)
            .clip(RoundedCornerShape(24.dp))
    ) {
        // 底層旋轉磨砂背景
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(40.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded)
                .graphicsLayer(alpha = alpha)
                .background(atmosphereBrush)
        )
        
        // 疊加微型粒子呼吸系統
        LightAppAtmosphereParticles()
    }
}

/**
 * ParticleData：定義單個星體的狀態 (終極閃爆版)
 */
data class ParticleData(
    val originX: Float,
    val originY: Float,
    val size: Float,
    val hue: Float,          // 多彩 Hue
    val alphaOffset: Float,
    val breathSpeed: Float,
    val maxAlpha: Float,
    val dirX: Float,
    val dirY: Float,
    val driftDistance: Float,
    val isBurstStar: Boolean // 閃爆標記
)

/**
 * LightAppAtmosphereParticles：主外框內部的呼吸粒子系統 (終極定案：高密度繽紛 Google 微型十字星芒閃爆版)
 */
@Composable
fun LightAppAtmosphereParticles() {
    val particleCount = 320
    val particles = remember {
        List(particleCount) {
            val angle = (0..360).random() * (Math.PI / 180f).toFloat()
            val isBurst = (0..100).random() < 25
            ParticleData(
                originX = (0..1000).random() / 1000f,
                originY = (0..1000).random() / 1000f,
                size = (35..75).random() / 10f,
                hue = (0..360).random().toFloat(),
                alphaOffset = (0..1000).random() / 1000f,
                // 加快呼吸頻率 (120..200)
                breathSpeed = if (isBurst) (120..200).random().toFloat() else (110..250).random().toFloat(),
                maxAlpha = if (isBurst) 0.95f else 0.45f + (0.4f * ((0..1000).random() / 1000f)),
                dirX = Math.cos(angle.toDouble()).toFloat(),
                dirY = Math.sin(angle.toDouble()).toFloat(),
                driftDistance = (250..550).random() / 10f,
                isBurstStar = isBurst
            )
        }
    }

    val time = RainbowEngine.getSharedTime()
    val starPath = remember { Path() }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        particles.forEach { p ->
            // 1. 正弦波脈衝呼吸，閃爆星帶有更陡峭的明滅曲線
            val rawBreath = Math.sin((time / p.breathSpeed) + p.alphaOffset * 2 * Math.PI).toFloat()
            val breath = if (p.isBurstStar) {
                Math.pow(((rawBreath + 1f) * 0.5f).toDouble(), 2.5).toFloat()
            } else {
                (rawBreath + 1f) * 0.5f
            }

            val alpha = p.maxAlpha * (0.1f + breath * 0.9f)

            // 2. 多方向靈動漂移
            val driftFactor = Math.sin((time / 454f) + p.alphaOffset * 2 * Math.PI).toFloat()
            val offsetX = p.dirX * p.driftDistance.dp.toPx() * driftFactor
            val offsetY = p.dirY * p.driftDistance.dp.toPx() * driftFactor

            val x = p.originX * w + offsetX
            val y = p.originY * h + offsetY

            // 繪製多彩繽紛星芒 (HSV 轉 Color)
            val color = Color.hsv(p.hue, 0.75f, 1.0f).copy(alpha = alpha)

            // 繪製四角 Google 十字星芒
            val radius = p.size.dp.toPx() * (0.7f + breath * 0.6f)
            starPath.reset()
            starPath.moveTo(x, y - radius)
            starPath.quadraticBezierTo(x, y, x + radius, y)
            starPath.quadraticBezierTo(x, y, x, y + radius)
            starPath.quadraticBezierTo(x, y, x - radius, y)
            starPath.quadraticBezierTo(x, y, x, y - radius)
            starPath.close()
            drawPath(starPath, color)
        }
    }
}

/**
 * ReligionFlashOverlay：專屬 UI 1 宗教卡片的滿屏核爆閃爆層
 */
@Composable
fun ReligionFlashOverlay() {
    val flashAlpha = remember { Animatable(0f) }
    
    LaunchedEffect(GlobalState.flashTrigger) {
        if (GlobalState.flashTrigger > 0) {
            flashAlpha.animateTo(1.0f, tween(100, easing = LinearEasing))
            delay(300)
            flashAlpha.animateTo(0f, tween(600, easing = LinearOutSlowInEasing))
            GlobalState.flashTrigger = 0L
        }
    }

    if (flashAlpha.value > 0f) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(GlobalState.flashColor.copy(alpha = flashAlpha.value))
        )
    }
}

/**
 * ReligionCardComposeView：實作 128 色動態外框與防裁切內容
 */
@Composable
fun ReligionCardComposeView(
    name: String,
    desc: String,
    themeColor: Color,
    isSelected: Boolean,
    isRainbowEnabled: Boolean
) {
    // 1. 文字呼吸動畫
    val textAlpha by if (isSelected) {
        val infiniteTransition = rememberInfiniteTransition(label = "TextBreathing")
        infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 0.4f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "Alpha"
        )
    } else {
        remember { mutableStateOf(1f) }
    }

    // 3. 冠名 TTS 預載觸發 Slot (模擬渲染進度至倒數第二個字時觸發)
    if (isSelected) {
        LaunchedEffect(isSelected) {
            delay(600) // 模擬文字繪製進度
            // [API SLOT] 發送輕量經緯度訊號，完成雲端冠名語音預載
            // Payload: { lat: ${GlobalState.globalLatitude}, lng: ${GlobalState.globalLongitude} }
        }
    }

    // 2. 128 色動態外框畫筆 (始終保持彩虹旋轉)
    val rotation = RainbowEngine.getSharedRotation()
    val rainbowBrush = remember(rotation) {
        object : ShaderBrush() {
            override fun createShader(size: Size): Shader {
                val shader = SweepGradient(size.center.x, size.center.y, RainbowEngine.colorInts, null)
                val matrix = Matrix()
                matrix.postRotate(rotation, size.center.x, size.center.y)
                shader.setLocalMatrix(matrix)
                return shader
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawWithContent {
                drawContent()
                // 3. 重新繪製 128 色卡片外框 (當開啟氛圍燈或被選中時)
                if (isRainbowEnabled || isSelected) {
                    drawRoundRect(
                        brush = rainbowBrush,
                        style = Stroke(width = if (isSelected) 3.5.dp.toPx() else 2.5.dp.toPx()),
                        cornerRadius = CornerRadius(28.dp.toPx())
                    )
                }
            }
            .padding(horizontal = 24.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 標題：實心填色
            BasicText(
                text = name,
                style = TextStyle(
                    color = themeColor.copy(alpha = textAlpha),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            )
            
            Spacer(modifier = Modifier.height(10.dp))
            
            // 描述：實心填色，確保完整顯示不被裁切
            BasicText(
                text = desc,
                style = TextStyle(
                    color = themeColor.copy(alpha = textAlpha * 0.85f),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
            )
        }
    }
}

/**
 * RainbowBorderWrapper (通用組件) - 支援主題色襯底
 */
@Composable
fun RainbowBorderWrapper(
    modifier: Modifier = Modifier,
    isEnabled: Boolean = GlobalState.isRainbowEnabled,
    isSelected: Boolean = false,
    cornerRadius: Dp = 12.dp,
    borderWidth: Dp = 2.dp,
    underlayColor: Color = Color.Transparent, // 新增：襯底顏色
    content: @Composable () -> Unit
) {
    if (!isEnabled) {
        Box(modifier = modifier) { content() }
        return
    }

    val rotation = RainbowEngine.getSharedRotation()
    val animatedBrush = remember(rotation) {
        object : ShaderBrush() {
            override fun createShader(size: Size): Shader {
                val shader = SweepGradient(size.center.x, size.center.y, RainbowEngine.colorInts, null)
                val matrix = Matrix()
                matrix.postRotate(rotation, size.center.x, size.center.y)
                shader.setLocalMatrix(matrix)
                return shader
            }
        }
    }

    Box(
        modifier = modifier.drawWithContent {
            // 1. 繪製主題色襯底 (若有)
            if (underlayColor != Color.Transparent) {
                drawRoundRect(
                    color = underlayColor,
                    cornerRadius = CornerRadius(cornerRadius.toPx()),
                    style = Stroke(width = borderWidth.toPx())
                )
            }
            
            drawContent()
            
            // 2. 繪製 128 色流光邊框
            drawRoundRect(
                brush = animatedBrush,
                style = Stroke(width = if (isSelected) borderWidth.toPx() * 1.5f else borderWidth.toPx()),
                cornerRadius = CornerRadius(cornerRadius.toPx())
            )
        }
    ) {
        content()
    }
}

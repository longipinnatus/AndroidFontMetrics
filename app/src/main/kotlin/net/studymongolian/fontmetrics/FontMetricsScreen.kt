package net.studymongolian.fontmetrics

import android.graphics.Paint
import android.graphics.Rect
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun FontMetricsScreen() {
    var text by remember { mutableStateOf("My text line") }
    var textSizeInput by remember { mutableStateOf("200") }
    var displayedTextSize by remember { mutableFloatStateOf(200f) }
    var displayedText by remember { mutableStateOf("My text line") }

    var isTopVisible by remember { mutableStateOf(true) }
    var isAscentVisible by remember { mutableStateOf(true) }
    var isBaselineVisible by remember { mutableStateOf(true) }
    var isDescentVisible by remember { mutableStateOf(true) }
    var isBottomVisible by remember { mutableStateOf(true) }
    var isBoundsVisible by remember { mutableStateOf(true) }
    var isWidthVisible by remember { mutableStateOf(true) }

    val focusManager = LocalFocusManager.current
    val textColor = MaterialTheme.colorScheme.onSurface.toArgb()

    val paint = remember(displayedTextSize, textColor) {
        Paint().apply {
            textSize = displayedTextSize
            isAntiAlias = true
            color = textColor
        }
    }
    val fm = paint.fontMetrics
    val bounds = Rect()
    paint.getTextBounds(displayedText, 0, displayedText.length, bounds)
    val measuredWidth = paint.measureText(displayedText)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(title = { Text("Font Metrics") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            FontMetricsViewCompose(
                text = displayedText,
                textSizePx = displayedTextSize,
                isTopVisible = isTopVisible,
                isAscentVisible = isAscentVisible,
                isBaselineVisible = isBaselineVisible,
                isDescentVisible = isDescentVisible,
                isBottomVisible = isBottomVisible,
                isBoundsVisible = isBoundsVisible,
                isWidthVisible = isWidthVisible,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(4.dp))
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = text,
                        onValueChange = { text = it },
                        label = { Text("Text") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = textSizeInput,
                        onValueChange = { textSizeInput = it },
                        label = { Text("Size") },
                        modifier = Modifier.width(80.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    FilledTonalButton(
                        onClick = {
                            displayedText = text
                            displayedTextSize = textSizeInput.toFloatOrNull() ?: 200f
                            focusManager.clearFocus()
                        },
                        modifier = Modifier.padding(top = 8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(ButtonDefaults.IconSize)
                        )
                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        Text("Update")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                MetricToggle("Top", isTopVisible, colorResource(R.color.top), fm.top.toString()) { isTopVisible = it }
                MetricToggle("Ascent", isAscentVisible, colorResource(R.color.ascent), fm.ascent.toString()) { isAscentVisible = it }
                MetricToggle("Baseline", isBaselineVisible, colorResource(R.color.baseline), "0.0") { isBaselineVisible = it }
                MetricToggle("Descent", isDescentVisible, colorResource(R.color.descent), fm.descent.toString()) { isDescentVisible = it }
                MetricToggle("Bottom", isBottomVisible, colorResource(R.color.bottom), fm.bottom.toString()) { isBottomVisible = it }
                MetricToggle("Measured width", isWidthVisible, colorResource(R.color.measured_width), measuredWidth.toString()) { isWidthVisible = it }
                val boundsText = stringResource(R.string.bounds_format, bounds.width(), bounds.height())
                MetricToggle("Text bounds", isBoundsVisible, colorResource(R.color.text_bounds), boundsText) { isBoundsVisible = it }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(32.dp))
                    Text(
                        "Leading",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.width(140.dp)
                    )
                    Text(
                        fm.leading.toString(),
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun MetricToggle(
    label: String,
    checked: Boolean,
    color: Color,
    value: String,
    onCheckedChange: (Boolean) -> Unit
) {
    CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(checkedColor = color)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(label, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.width(140.dp))
            Text(value, color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun FontMetricsViewCompose(
    text: String,
    textSizePx: Float,
    isTopVisible: Boolean,
    isAscentVisible: Boolean,
    isBaselineVisible: Boolean,
    isDescentVisible: Boolean,
    isBottomVisible: Boolean,
    isBoundsVisible: Boolean,
    isWidthVisible: Boolean,
    modifier: Modifier = Modifier
) {
    val topColor = colorResource(R.color.top)
    val ascentColor = colorResource(R.color.ascent)
    val baselineColor = colorResource(R.color.baseline)
    val descentColor = colorResource(R.color.descent)
    val bottomColor = colorResource(R.color.bottom)
    val measuredWidthColor = colorResource(R.color.measured_width)
    val textBoundsColor = colorResource(R.color.text_bounds)
    val textColor = MaterialTheme.colorScheme.onSurface.toArgb()

    Canvas(modifier = modifier) {
        val paint = Paint().apply {
            textSize = textSizePx
            isAntiAlias = true
            color = textColor
        }

        val horizontalOffset = 10.dp.toPx()
        val verticalAdjustment = size.height / 2f
        
        translate(top = verticalAdjustment) {
            val fm = paint.fontMetrics
            val stopX = size.width

            if (isTopVisible) {
                drawLine(topColor, Offset(0f, fm.top), Offset(stopX, fm.top), strokeWidth = 2.dp.toPx())
            }
            if (isAscentVisible) {
                drawLine(ascentColor, Offset(0f, fm.ascent), Offset(stopX, fm.ascent), strokeWidth = 2.dp.toPx())
            }
            if (isBaselineVisible) {
                drawLine(baselineColor, Offset(0f, 0f), Offset(stopX, 0f), strokeWidth = 2.dp.toPx())
            }
            if (isDescentVisible) {
                drawLine(descentColor, Offset(0f, fm.descent), Offset(stopX, fm.descent), strokeWidth = 2.dp.toPx())
            }
            if (isBottomVisible) {
                drawLine(bottomColor, Offset(0f, fm.bottom), Offset(stopX, fm.bottom), strokeWidth = 2.dp.toPx())
            }

            translate(left = horizontalOffset) {
                drawContext.canvas.nativeCanvas.drawText(text, 0f, 0f, paint)

                if (isBoundsVisible) {
                    val bounds = Rect()
                    paint.getTextBounds(text, 0, text.length, bounds)
                    drawRect(
                        color = textBoundsColor,
                        topLeft = Offset(bounds.left.toFloat(), bounds.top.toFloat()),
                        size = Size(bounds.width().toFloat(), bounds.height().toFloat()),
                        style = Stroke(width = 2.dp.toPx())
                    )
                }

                if (isWidthVisible) {
                    val width = paint.measureText(text)
                    val bounds = Rect()
                    paint.getTextBounds(text, 0, text.length, bounds)
                    val lineStartX = bounds.left - (width - bounds.width()) / 2f
                    
                    drawLine(measuredWidthColor, Offset(lineStartX, -verticalAdjustment), Offset(lineStartX, -verticalAdjustment + size.height), strokeWidth = 2.dp.toPx())
                    val lineEndX = lineStartX + width
                    drawLine(measuredWidthColor, Offset(lineEndX, -verticalAdjustment), Offset(lineEndX, -verticalAdjustment + size.height), strokeWidth = 2.dp.toPx())
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FontMetricsScreenPreview() {
    AndroidFontMetricsTheme {
        FontMetricsScreen()
    }
}

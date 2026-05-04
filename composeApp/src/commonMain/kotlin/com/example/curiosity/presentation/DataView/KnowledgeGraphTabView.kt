package com.example.curiosity.presentation.DataView

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isCtrlPressed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.curiosity.core.models.FactRelationshipTypes
import com.example.curiosity.core.models.ResultData
import com.example.curiosity.core.models.ScreenSizeState
import com.example.curiosity.theme.BlackBackgroundShade
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun KnowledgeGraphTabView(
    screenSizeState: ScreenSizeState,
    query: String,
    resultsData: ResultData,
){

    val factRelationships = resultsData.resultFactRelationships
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val textMeasurer = rememberTextMeasurer()

    Column(
        modifier = Modifier.padding(top = 10.dp, bottom = if (screenSizeState== ScreenSizeState.EXPANDED) 30.dp else 90.dp
            , start = 25.dp, end = 25.dp),
    ) {
        // Query
        Text(
            text = query,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.SemiBold,
            ),
            color = Color.LightGray,
            textAlign = TextAlign.Start,
            maxLines = 3,
            modifier = Modifier.padding(bottom = 20.dp)
        )
        // Knowledge Graph Heading
        Text(
            text = "Knowledge Graph",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = Color.LightGray,
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(bottom = 15.dp)
        )
        // Knowledge Graph Box
        BoxWithConstraints(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clipToBounds()
                .clip(RoundedCornerShape(10.dp))
                .background(BlackBackgroundShade)
                // DESKTOP SCROLL ZOOM
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            event.changes.forEach { change ->
                                if (change.scrollDelta != Offset.Zero) {
                                    val zoomFactor = 1f - (change.scrollDelta.y * 0.001f)
                                    val oldScale = scale
                                    scale = (scale * zoomFactor).coerceIn(0.1f, 10f)
                                    val cursor = change.position
                                    offset = (offset + cursor / oldScale) -
                                            (cursor / scale)
                                    change.consume()
                                }
                            }
                        }
                    }
                } // MOBILE PINCHES
                .pointerInput(Unit) {
                    detectTransformGestures { centroid, pan, zoom, _ ->
                        val oldScale = scale
                        scale = (scale * zoom).coerceIn(0.1f, 10f)
                        offset = (offset + centroid / oldScale) - (centroid / scale + pan / oldScale)
                    }
                }
        ) {
            // Knowledge Graph Canvas
            Canvas(modifier = Modifier.fillMaxSize()) {
                val gridBase = 80f
                val currentGrid = gridBase * scale
                val startX = ((-offset.x * scale) % currentGrid)
                val startY = ((-offset.y * scale) % currentGrid)

                // GRID LINES
                var x = startX
                while (x < size.width + currentGrid) {
                    drawLine(
                        color = Color.LightGray.copy(alpha = 0.5f),
                        start = Offset(x, 0f),
                        end = Offset(x, size.height),
                        strokeWidth = 1f
                    )
                    x += currentGrid
                }
                var y = startY
                while (y < size.height + currentGrid) {
                    drawLine(
                        color = Color.LightGray.copy(alpha = 0.5f),
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = 1f
                    )
                    y += currentGrid
                }
                withTransform(
                    transformBlock = {
                        // DATA PREP & LAYOUT CALCULATION
                        val allClaims = (factRelationships.map { it.source_claim } + factRelationships.map { it.target_claim }).distinct()
                        val nodeWidth = 350f
                        val nodeHeight = 180f
                        val horizontalGap = 550f
                        val verticalGap = 350f

                        val nodePositions = allClaims.mapIndexed { index, claim ->
                            claim to Offset(x = (index % 3) * horizontalGap, y = (index / 3) * verticalGap)
                        }.toMap()

                        if (nodePositions.isNotEmpty()) {
                            // CALCULATE BOUNDING BOX
                            val minX = nodePositions.values.minOf { it.x }
                            val minY = nodePositions.values.minOf { it.y }
                            val maxX = nodePositions.values.maxOf { it.x } + nodeWidth
                            val maxY = nodePositions.values.maxOf { it.y } + nodeHeight

                            val contentWidth = (maxX - minX).coerceAtLeast(1f)
                            val contentHeight = (maxY - minY).coerceAtLeast(1f)
                            val contentCenterX = (minX + maxX) / 2f
                            val contentCenterY = (minY + maxY) / 2f

                            // AUTO-FIT & AUTO-CENTER LOGIC
                            val paddingFactor = 0.8f
                            val fitScale = minOf(
                                (size.width * paddingFactor) / contentWidth,
                                (size.height * paddingFactor) / contentHeight
                            ).coerceIn(0.1f, 1.2f)

                            // Switch between Auto-Layout (initial) and Manual-Layout (after user panned)
                            val finalScale = if (offset == Offset.Zero && scale == 1f) fitScale else scale
                            val finalOffset = if (offset == Offset.Zero) {
                                Offset(
                                    contentCenterX - (size.width / 2f / finalScale),
                                    contentCenterY - (size.height / 2f / finalScale)
                                )
                            } else offset

                            // APPLY TRANSFORMATIONS
                            scale(finalScale, pivot = Offset.Zero)
                            translate(-finalOffset.x * finalScale, -finalOffset.y * finalScale)
                        }
                    },
                    drawBlock = {
                        // RE-CALCULATE DATA FOR DRAWING
                        val allClaims = (factRelationships.map { it.source_claim } + factRelationships.map { it.target_claim }).distinct()
                        val nodeWidth = 350f
                        val nodeHeight = 180f
                        val horizontalGap = 550f
                        val verticalGap = 350f

                        val nodePositions = allClaims.mapIndexed { index, claim ->
                            claim to Offset(x = (index % 3) * horizontalGap, y = (index / 3) * verticalGap)
                        }.toMap()

                        // Sync with the same finalScale/finalOffset logic
                        val minX = nodePositions.values.minOfOrNull { it.x } ?: 0f
                        val minY = nodePositions.values.minOfOrNull { it.y } ?: 0f
                        val maxX = (nodePositions.values.maxOfOrNull { it.x } ?: 0f) + nodeWidth
                        val maxY = (nodePositions.values.maxOfOrNull { it.y } ?: 0f) + nodeHeight
                        val fitScale = minOf((size.width * 0.8f) / (maxX - minX).coerceAtLeast(1f), (size.height * 0.8f) / (maxY - minY).coerceAtLeast(1f)).coerceIn(0.1f, 1.2f)
                        val currentDrawingScale = if (offset == Offset.Zero && scale == 1f) fitScale else scale

                        // DRAW RELATIONSHIPS (Arrows)
                        factRelationships.forEach { rel ->
                            val startPos = nodePositions[rel.source_claim] ?: return@forEach
                            val endPos = nodePositions[rel.target_claim] ?: return@forEach

                            val startCenter = startPos + Offset(nodeWidth / 2, nodeHeight / 2)
                            val endCenter = endPos + Offset(nodeWidth / 2, nodeHeight / 2)

                            val dx = endCenter.x - startCenter.x
                            val dy = endCenter.y - startCenter.y
                            val absDx = kotlin.math.abs(dx).coerceAtLeast(0.001f)
                            val absDy = kotlin.math.abs(dy).coerceAtLeast(0.001f)

                            val tanLine = absDy / absDx
                            val tanBox = nodeHeight / nodeWidth

                            val (targetEdgeX, targetEdgeY) = if (tanLine > tanBox) {
                                val signY = if (dy > 0) 1 else -1
                                (dx * (nodeHeight / 2) / absDy) to (signY * nodeHeight / 2)
                            } else {
                                val signX = if (dx > 0) 1 else -1
                                (signX * nodeWidth / 2) to (dy * (nodeWidth / 2) / absDx)
                            }

                            val targetEdge = endCenter - Offset(targetEdgeX.toFloat(), targetEdgeY.toFloat())
                            val sourceEdge = startCenter + Offset(targetEdgeX.toFloat(), targetEdgeY.toFloat())

                            val relColor = when (rel.connection_type) {
                                FactRelationshipTypes.SUPPORTS -> Color(0xFF4CAF50)
                                FactRelationshipTypes.CONTRADICTS -> Color(0xFFF44336)
                                FactRelationshipTypes.CAUSES -> Color(0xFF2196F3)
                                FactRelationshipTypes.ELABORATES -> Color(0xFF9C27B0)
                                else -> Color.LightGray
                            }

                            drawCircle(
                                color = relColor,
                                radius = 12f / currentDrawingScale,
                                center = sourceEdge
                            )

                            drawLine(
                                color = relColor,
                                start = sourceEdge,
                                end = targetEdge,
                                strokeWidth = 4f / currentDrawingScale
                            )
                            drawArrowHead(sourceEdge, targetEdge, relColor, currentDrawingScale)
                        }

                        // DRAW NODES (Sticky Notes)
                        nodePositions.forEach { (claim, pos) ->
                            // Sticky Note Background
                            drawRoundRect(
                                color = Color(0xFFFFF9C4),
                                topLeft = pos,
                                size = Size(nodeWidth, nodeHeight),
                                cornerRadius = CornerRadius(12f, 12f)
                            )

                            // Wrapped & Centered Text
                            val textStyle = TextStyle(
                                color = Color.Black,
                                fontSize = 12.sp / currentDrawingScale,
                                lineHeight = 16.sp / currentDrawingScale,
                                textAlign = TextAlign.Start
                            )

                            val measuredText = textMeasurer.measure(
                                text = claim,
                                style = textStyle,
                                constraints = Constraints(
                                    maxWidth = (nodeWidth - 32f).toInt().coerceAtLeast(0),
                                    maxHeight = (nodeHeight - 20f).toInt().coerceAtLeast(0)
                                ),
                                softWrap = true,
                                overflow = TextOverflow.Ellipsis
                            )

                            val textTopLeft = Offset(
                                x = pos.x + (nodeWidth / 2) - (measuredText.size.width / 2),
                                y = pos.y + (nodeHeight / 2) - (measuredText.size.height / 2)
                            )

                            drawText(textLayoutResult = measuredText, topLeft = textTopLeft)
                        }
                    }
                )
            }
        }
        // Arrow Type Description
        Row (
            modifier = Modifier.fillMaxWidth().padding(top = 15.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            FactRelationshipTypes.entries.forEach { type ->
                val color = when (type) {
                    FactRelationshipTypes.SUPPORTS -> Color(0xFF4CAF50)
                    FactRelationshipTypes.CONTRADICTS -> Color(0xFFF44336)
                    FactRelationshipTypes.CAUSES -> Color(0xFF2196F3)
                    FactRelationshipTypes.ELABORATES -> Color(0xFF9C27B0)
                    else -> Color.LightGray
                }
                if (type!=FactRelationshipTypes.UNKNOWN) {
                    Text(
                        "→ " + type.name,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                        ),
                        color = color
                    )
                }
            }
        }
    }
}


fun DrawScope.drawArrowHead(start: Offset, end: Offset, color: Color, scale: Float) {
    val angle = atan2(end.y - start.y, end.x - start.x)
    val arrowSize = 25f / scale
    val path = Path().apply {
        moveTo(end.x, end.y)
        lineTo(
            end.x - arrowSize * cos(angle - PI.toFloat() / 6),
            end.y - arrowSize * sin(angle - PI.toFloat() / 6)
        )
        moveTo(end.x, end.y)
        lineTo(
            end.x - arrowSize * cos(angle + PI.toFloat() / 6),
            end.y - arrowSize * sin(angle + PI.toFloat() / 6)
        )
    }
    drawPath(path, color, style = Stroke(width = 5f / scale, cap = StrokeCap.Round))
}


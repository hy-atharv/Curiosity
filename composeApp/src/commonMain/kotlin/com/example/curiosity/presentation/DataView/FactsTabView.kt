package com.example.curiosity.presentation.DataView

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.curiosity.core.models.ResultData
import com.example.curiosity.theme.BlackBackgroundShade
import com.example.curiosity.theme.ElectricBlue
import curiosity.composeapp.generated.resources.Res
import curiosity.composeapp.generated.resources.pin
import org.jetbrains.compose.resources.painterResource

@Composable
fun FactsTabView(
    query: String,
    resultsData: ResultData,
    openSourcePage: (String) -> Unit
){
    val facts = resultsData.resultFacts

    val ticketShape = TicketShape(8.dp)

    val expandedEvidencesIndices = remember {
        mutableStateListOf<Int>()
    }

    LazyColumn(
        contentPadding = PaddingValues(top = 10.dp, bottom = 90.dp, start = 25.dp, end = 25.dp),
    ) {
        // Query
        item {
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
        }
        // Facts Heading
        item {
            Text(
                text = "Key Assertions",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = Color.LightGray,
                textAlign = TextAlign.Start,
            )
        }
        // Facts
        items(facts.size){ index ->
            Box(
                modifier = Modifier.fillMaxWidth().padding(vertical = 15.dp)
            ){
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .graphicsLayer {
                            shape = ticketShape
                            clip = true
                        }
                        .background(Color(0xFFFFF9C4).copy(alpha = 0.9f))
                )
                Column(
                    modifier = Modifier.fillMaxWidth().padding(all = 10.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.Start
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.pin),
                        contentDescription = "Pin",
                        tint = Color.Unspecified,
                        modifier = Modifier.align(Alignment.CenterHorizontally).size(25.dp).offset(y = (-15).dp)
                    )
                    Text(
                        text = facts[index].claim,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        ),
                        color = Color.Black,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.padding(all = 25.dp)
                    )
                    // Evidences
                    Column(
                        modifier = Modifier.fillMaxWidth()
                            .padding(bottom = 15.dp, start = 25.dp, end = 25.dp),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Evidences",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 1.sp,
                                textDecoration = TextDecoration.Underline
                            ),
                            color = Color.Black,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.padding(bottom = 10.dp)
                                .clickable{
                                    // Toggle Claim Evidences
                                    if (expandedEvidencesIndices.contains(index)){
                                        expandedEvidencesIndices.remove(index)
                                    }
                                    else{
                                        expandedEvidencesIndices.add(index)
                                    }
                                }
                        )
                        if (expandedEvidencesIndices.contains(index)){
                            facts[index].evidence_urls.forEach { url ->
                                Text(
                                    text = "→ "+url.substringAfter("//").substringBefore("/").removePrefix("www."),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        letterSpacing = 1.sp,
                                        fontWeight = FontWeight.SemiBold,
                                    ),
                                    color = Color(0xFF00008B),
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier.padding(bottom = 5.dp)
                                        .clickable{
                                            // Open Page
                                            openSourcePage(url.trim())
                                        }
                                        .pointerHoverIcon(PointerIcon.Hand)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

fun TicketShape(circleRadius: Dp): Shape = GenericShape { size, _ ->
    val radiusPx = circleRadius.value * 3 // Adjust scale for density

    moveTo(0f, 0f)
    // Left edge with cutout
    lineTo(0f, size.height / 2 - radiusPx)
    arcTo(
        rect = Rect(
            left = -radiusPx,
            top = size.height / 2 - radiusPx,
            right = radiusPx,
            bottom = size.height / 2 + radiusPx
        ),
        startAngleDegrees = 270f,
        sweepAngleDegrees = 180f,
        forceMoveTo = false
    )
    lineTo(0f, size.height)

    // Bottom edge
    lineTo(size.width, size.height)

    // Right edge with cutout
    lineTo(size.width, size.height / 2 + radiusPx)
    arcTo(
        rect = Rect(
            left = size.width - radiusPx,
            top = size.height / 2 - radiusPx,
            right = size.width + radiusPx,
            bottom = size.height / 2 + radiusPx
        ),
        startAngleDegrees = 90f,
        sweepAngleDegrees = 180f,
        forceMoveTo = false
    )
    lineTo(size.width, 0f)

    // Top edge
    close()
}
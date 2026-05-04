package com.example.curiosity.presentation.DataView


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter.Companion.tint
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.curiosity.core.models.ScreenSizeState
import com.example.curiosity.theme.ElectricBlue
import com.example.curiosity.theme.GoldenYellow
import com.example.curiosity.theme.VividMagenta
import curiosity.composeapp.generated.resources.Res
import curiosity.composeapp.generated.resources.queries_roadmap_arrow
import org.jetbrains.compose.resources.painterResource

@Composable
fun QueriesCanvasView(
    screenSizeState: ScreenSizeState,
    queriesAndSummaries: List<Pair<String, String>>, // <Query, Summary>
    enableResultsCanvas: (String) -> Unit
){
//    val queriesAndSummaries = queriesAndSummaries.toMutableList()
//    queriesAndSummaries.addAll(
//        listOf(
//            "How do LLMs use attention mechanisms?" to "Large Language Models use self-attention to weigh the importance of different words in a sequence, allowing them to understand context and long-range dependencies.",
//            "Benefits of Kotlin Multiplatform (KMP)" to "KMP allows developers to share business logic across iOS, Android, and Web while maintaining native performance and UI flexibility.",
//            "What is a Server-Sent Event (SSE)?" to "SSE is a standard allowing servers to push real-time updates to web pages over HTTP, specifically useful for streaming data like live scores or chat.",
//            "How to optimize Room database queries?" to "Optimization involves using indices on frequently searched columns, leveraging Write-Ahead Logging (WAL), and avoiding unnecessary observable queries.",
//            "Difference between Flow and SharedFlow" to "Flow is cold and handles a single collector, while SharedFlow is hot, allowing multiple collectors to receive the same broadcasted data.",
//            "Best practices for Jetpack Compose performance" to "Key practices include using 'derivedStateOf' to minimize recompositions and ensuring heavy logic happens outside the Composable functions.",
//            "What is the Actix Web framework in Rust?" to "Actix Web is a powerful, pragmatic, and extremely fast web framework for Rust, built on top of the Actor system for high concurrency.",
//            "How does CoroutineScope differ from SupervisorScope?" to "In a CoroutineScope, one failure cancels all siblings; in a SupervisorScope, a child's failure does not affect its siblings or the parent.",
//            "Common causes of Ktor SSE timeouts" to "Timeouts often occur due to default HTTP client settings, server-side buffering, or missing double-newlines (\\n\\n) in the event stream.",
//            "Why use UseCases in Clean Architecture?" to "UseCases encapsulate specific business logic, making the code more testable, reusable, and independent of UI or data source changes."
//        )
//    )

    val colors = listOf<Color>(
        GoldenYellow,
        ElectricBlue,
        VividMagenta
    )

    val gradientBrush = remember {
        Brush.linearGradient(colors)
    }

    LazyColumn(
       contentPadding = PaddingValues(top = 20.dp, bottom = 90.dp),
    ) {
        // Heading
        item {
            Row(
                modifier = Modifier.fillMaxWidth(
                    when(screenSizeState) {
                        ScreenSizeState.COMPACT -> 1f
                        ScreenSizeState.MEDIUM -> 1f
                        ScreenSizeState.EXPANDED -> 0.9f
                    }
                )
                    .padding(bottom = 20.dp),
            ) {
                Text(
                    text = "Your research journey, from the first spark to the latest find.",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        brush = gradientBrush,
                        fontWeight = FontWeight.SemiBold
                    ),
                    textAlign = TextAlign.Start,
                    modifier = Modifier.padding(horizontal = 25.dp)
                )
            }
        }
        items(queriesAndSummaries.size){ index ->
            val reverseIndex = (queriesAndSummaries.size - 1) - index
            // ROADMAP ARROW
            if (reverseIndex != queriesAndSummaries.size - 1){
                Box(
                    modifier = Modifier.fillMaxWidth(
                        when(screenSizeState) {
                            ScreenSizeState.COMPACT -> 1f
                            ScreenSizeState.MEDIUM -> 1f
                            ScreenSizeState.EXPANDED -> 0.9f
                        }
                    ),
                    contentAlignment = Alignment.Center
                ){
                    Icon(
                        painter = painterResource(Res.drawable.queries_roadmap_arrow),
                        contentDescription = "Arrow",
                        tint = Color.Unspecified,
                        modifier = Modifier
                            .size(25.dp)
                            .graphicsLayer(alpha = 0.99f) // 2. Required for some blending modes
                            .drawWithContent {
                                drawContent() // Draw the icon first
                                drawRect(
                                    brush = gradientBrush,
                                    blendMode = BlendMode.SrcIn // 3. Masks the gradient to the icon shape
                                )
                            }
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(
                    when(screenSizeState) {
                        ScreenSizeState.COMPACT -> 1f
                        ScreenSizeState.MEDIUM -> 1f
                        ScreenSizeState.EXPANDED -> 0.9f
                    }
                )
                    .padding(vertical = 10.dp)
                    .clickable{
                        // OPEN QUERY SEARCH RESULTS
                        enableResultsCanvas(queriesAndSummaries[reverseIndex].first)
                    }
                    .pointerHoverIcon(PointerIcon.Hand),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ){
                Column(
                    modifier = if (reverseIndex==0 && queriesAndSummaries.size!=1){
                        Modifier
                            .dropShadow(
                                shape = RoundedCornerShape(20.dp),
                                shadow = Shadow(
                                    radius = 100.dp,
                                    spread = 20.dp,
                                    color = GoldenYellow,
                                    alpha = 0.2f
                                )
                            )
                            .padding(horizontal = 25.dp, vertical = 10.dp)
                    }
                    else{
                        Modifier.padding(horizontal = 25.dp)
                    },
                    verticalArrangement = Arrangement.Center
                ) {
                    // QUERY
                    Text(
                        text = queriesAndSummaries[reverseIndex].first,
                        style = if (screenSizeState == ScreenSizeState.EXPANDED){
                            MaterialTheme.typography.titleLarge.copy(
                                fontSize = 20.sp
                            )
                        }
                        else{
                            MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 18.sp
                            )
                        },
                        color = Color.White,
                        textAlign = TextAlign.Start,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(bottom = 5.dp)
                    )
                    // RESULT SUMMARY
                    Text(
                        text = queriesAndSummaries[reverseIndex].second,
                        style = if (screenSizeState == ScreenSizeState.EXPANDED){
                            MaterialTheme.typography.bodyLarge
                        }
                        else{
                            MaterialTheme.typography.bodyMedium
                        },
                        color = Color.LightGray,
                        textAlign = TextAlign.Start,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}
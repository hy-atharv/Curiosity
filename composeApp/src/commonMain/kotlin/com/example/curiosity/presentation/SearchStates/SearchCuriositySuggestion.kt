package com.example.curiosity.presentation.SearchStates

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.example.curiosity.core.models.ScreenSizeState
import com.example.curiosity.core.navigation.SearchAndDiscoveryChatScreen
import com.example.curiosity.theme.BlackBackgroundShade
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun BoxScope.SearchCuriositySuggestion(
    screenSizeState: ScreenSizeState,
    brush: Brush,
    searchId: Int,
    curiousQuestion: String,
    curiousQuestionTopic: String,
    navigator: NavHostController,
    disableCurrentCuriousQuestionBox: () -> Unit
){
    var isCuriousQuestionVisible by remember {
        mutableStateOf(false)
    }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit){
        isCuriousQuestionVisible = true
    }

    AnimatedVisibility(
        visible = isCuriousQuestionVisible,
        enter = slideInVertically(
            initialOffsetY = { -it / 2 }   // drop down
        ) + expandHorizontally(
            expandFrom = Alignment.End     // wipe left
        ) + fadeIn(),
        exit = shrinkHorizontally(
            shrinkTowards = Alignment.Start // wipe right
        ) + fadeOut(),
        modifier = Modifier.zIndex(5f)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 35.dp)
                .offset(
                    y = when (screenSizeState) {
                        ScreenSizeState.COMPACT -> (-5).dp
                        ScreenSizeState.MEDIUM -> (-10).dp
                        ScreenSizeState.EXPANDED -> (-15).dp
                    }
                )
                .widthIn(
                    min = when (screenSizeState) {
                        ScreenSizeState.COMPACT -> 100.dp
                        ScreenSizeState.MEDIUM -> 200.dp
                        ScreenSizeState.EXPANDED -> 300.dp
                    },
                    max = when (screenSizeState) {
                        ScreenSizeState.COMPACT -> 300.dp
                        ScreenSizeState.MEDIUM -> 300.dp
                        ScreenSizeState.EXPANDED -> 450.dp
                    }
                )
                .drawBehind {

                    val tailWidth = 16.dp.toPx()
                    val tailHeight = 12.dp.toPx()

                    val path = Path().apply {
                        // Top-end triangle
                        moveTo(size.width - tailWidth, 0f)
                        lineTo(size.width, -tailHeight)
                        lineTo(size.width, 0f)
                        close()
                    }

                    drawPath(
                        path = path,
                        color = BlackBackgroundShade
                    )
                }
                .dropShadow(
                    shape = RoundedCornerShape(
                        topStart = 20.dp,
                        bottomStart = 20.dp,
                        bottomEnd = 20.dp
                    ),
                    shadow = Shadow(
                        radius = 20.dp,
                        spread = 0.dp,
                        color = Color.Black,
                        alpha = 0.25f
                    )
                )
                .clip(
                    RoundedCornerShape(
                        topStart = 20.dp,
                        bottomStart = 20.dp,
                        bottomEnd = 20.dp
                    )
                )
                .background(BlackBackgroundShade)
                .zIndex(5f),
            contentAlignment = Alignment.Center
        )
        {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Text(
                    text = curiousQuestion,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        brush = brush
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Start,
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.padding(top = 10.dp).align(Alignment.End)
                ) {
                    Text(
                        text = "No, thanks",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Color.Gray,
                            fontWeight = FontWeight.SemiBold
                        ),
                        textAlign = TextAlign.Start,
                        modifier = Modifier.padding(start = 20.dp)
                            .clickable{
                                // LOGIC TO CLOSE DISCOVER SUGGESTION
                                isCuriousQuestionVisible = false
                                coroutineScope.launch {
                                    delay(300)
                                    disableCurrentCuriousQuestionBox()
                                }
                            }
                    )
                    Text(
                        text = "Discover",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        ),
                        textAlign = TextAlign.Start,
                        modifier = Modifier.padding(start = 20.dp).clickable{
                            // LOGIC TO NAVIGATE TO DISCOVERY CHATS
                            disableCurrentCuriousQuestionBox()
                            navigator.navigate(SearchAndDiscoveryChatScreen(
                                searchId,
                                curiousQuestionTopic,
                                curiousQuestion
                            ))
                        }
                    )
                }
            }
        }
    }
}
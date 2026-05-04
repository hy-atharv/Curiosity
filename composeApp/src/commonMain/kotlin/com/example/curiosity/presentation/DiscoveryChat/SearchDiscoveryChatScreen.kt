package com.example.curiosity.presentation.DiscoveryChat

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.insert
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.curiosity.core.models.CurrentUserSessionDataViewModel
import com.example.curiosity.core.models.ScreenSizeState
import com.example.curiosity.presentation.SearchStates.ActiveTextFieldMessageSendIcon
import com.example.curiosity.presentation.SearchStates.InactiveTextFieldSearchIcon
import com.example.curiosity.presentation.SearchStates.bottomAnimatedTaperedGradientBorder
import com.example.curiosity.theme.BlackBackgroundShade
import com.example.curiosity.theme.CuriosityTheme
import com.example.curiosity.theme.ElectricBlue
import com.example.curiosity.theme.GoldenYellow
import com.example.curiosity.theme.VividMagenta
import curiosity.composeapp.generated.resources.Res
import curiosity.composeapp.generated.resources.back
import curiosity.composeapp.generated.resources.response_loader_icon
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SearchDiscoveryChatScreen(
    navigator: NavHostController,
    searchId: Int,
    searchQuestionTopic: String,
    searchQuestionQuery: String,
    currentUserSessionDataViewModel: CurrentUserSessionDataViewModel,
    viewModel: SearchDiscoveryChatScreenViewModel = koinViewModel<SearchDiscoveryChatScreenViewModel>()
){
    val screenWidth = viewModel.widthState.collectAsState()

    val screenSizeState = ScreenSizeState.fromWidth(screenWidth.value)

    val messageFieldState = rememberTextFieldState()

    val interactionSource = remember { MutableInteractionSource() }

    val keyboardController = LocalSoftwareKeyboardController.current

    val currentResponseState by viewModel.currentResponseLoadingState.collectAsState()

    val chatsList by viewModel.currentDiscoveryChats.collectAsState()

    val listState = rememberLazyListState()

    val infiniteTransition = rememberInfiniteTransition()

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    LaunchedEffect(Unit){
        viewModel.scrollEvents.collect {
            if (chatsList.isNotEmpty()){
                listState.animateScrollToItem(chatsList.size - 1)
            }
        }
    }

    LaunchedEffect(Unit){
        if (chatsList.isNotEmpty()){
            listState.animateScrollToItem(chatsList.size - 1)
        }
        currentUserSessionDataViewModel.searchCuriosityAndDiscoveryDataMap.collect{ chatMap ->
            val chatsData = chatMap[searchId]
            if (chatsData != null){
                // Question Chat Different
                if (searchQuestionQuery!=chatsData.searchInitialQuery){
                    // New Discovery Starts
                    viewModel.resetDiscoveryChats()
                    viewModel.onMessageQuerySent(searchQuestionQuery)
                } else {
                    // Load Prev chats
                    viewModel.loadDiscoveryChats(chatsData.searchDiscoveryChats)
                }
            }
            else{
                if (searchQuestionQuery.isNotEmpty()){
                    viewModel.onMessageQuerySent(searchQuestionQuery)
                }
            }
        }
    }

    var lineCount by remember { mutableStateOf(1) }

    val colors = listOf<Color>(
        GoldenYellow,
        ElectricBlue,
        VividMagenta
    )

    val gradientBrush = remember {
        Brush.linearGradient(colors)
    }

    CuriosityTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Black)
                .windowInsetsPadding(WindowInsets.systemBars),
            color = Color.Black
        ){

            BoxWithConstraints(
                modifier = Modifier.fillMaxWidth(),
            ){
                viewModel.onWidthChange(maxWidth.value.toInt())
                Column(
                    Modifier.fillMaxSize()
                ) {
                    // TOP  BAR
                    Box(
                        modifier = when (currentResponseState) {
                            true -> Modifier.bottomAnimatedTaperedGradientBorder(
                                baseColors = colors
                            )

                            false -> Modifier
                        },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            modifier = Modifier.padding(
                                vertical = when (screenSizeState) {
                                    ScreenSizeState.COMPACT -> 10.dp
                                    ScreenSizeState.MEDIUM -> 15.dp
                                    ScreenSizeState.EXPANDED -> 20.dp
                                },
                                horizontal = 15.dp
                            )
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // NAVIGATE BACK TO SEARCH RESULTS
                            Box(
                                modifier = Modifier.size(45.dp)
                                    .clip(CircleShape)
                                    .background(BlackBackgroundShade)
                                    .border(
                                        width = 0.75.dp,
                                        color = Color.DarkGray,
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        // NAVIGATE BACK TO SEARCH RESULTS
                                        navigator.popBackStack()
                                    }
                                    .pointerHoverIcon(PointerIcon.Default),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(Res.drawable.back),
                                    tint = Color.White,
                                    contentDescription = "Back to query results",
                                    modifier = Modifier.padding(13.dp).fillMaxSize()
                                        .offset(x = -(2.dp))
                                )
                            }
                            // CURRENT QUESTION TOPIC HEADER
                            Box(
                                modifier = Modifier.align(Alignment.CenterVertically)
                                    .padding(horizontal = 10.dp)
                                    .widthIn(
                                        max = when (screenSizeState) {
                                            ScreenSizeState.COMPACT -> 200.dp
                                            ScreenSizeState.MEDIUM -> 250.dp
                                            ScreenSizeState.EXPANDED -> 400.dp
                                        }
                                    )
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(BlackBackgroundShade)
                                    .border(
                                        width = 0.75.dp,
                                        color = Color.DarkGray,
                                        shape = RoundedCornerShape(20.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = searchQuestionTopic.ifEmpty { "New Discovery" },
                                    style = when (screenSizeState) {
                                        ScreenSizeState.COMPACT -> MaterialTheme.typography.bodyLarge.copy(
                                            brush = gradientBrush
                                        )

                                        ScreenSizeState.MEDIUM -> MaterialTheme.typography.bodyLarge.copy(
                                            brush = gradientBrush
                                        )

                                        ScreenSizeState.EXPANDED -> MaterialTheme.typography.titleLarge.copy(
                                            brush = gradientBrush
                                        )
                                    },
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(
                                        horizontal = 20.dp,
                                        vertical = 10.dp
                                    )
                                )
                            }
                            // EMPTY BOX TO CENTER HEADER
                            Box(modifier = Modifier.size(45.dp))
                        }
                    }
                    // SEARCH DISCOVERY CHATS AREA AND BOTTOM CHAT TEXT FIELD
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(
                                when (screenSizeState) {
                                    ScreenSizeState.COMPACT -> 1f
                                    ScreenSizeState.MEDIUM -> 1f
                                    ScreenSizeState.EXPANDED -> 0.9f
                                }
                            ).padding(horizontal = 25.dp),
                            verticalArrangement = Arrangement.Bottom,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // CHAT BUBBLES AND RESPONSES AREA
                            LazyColumn(
                                contentPadding = PaddingValues(top = 10.dp, bottom = 90.dp),
                                state = listState,
                            ) {
                                items(chatsList.size) { index ->
                                    Box(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentAlignment = if (chatsList[index].first == "user") Alignment.CenterEnd else Alignment.CenterStart
                                    ) {
                                        // USER
                                        if (chatsList[index].first == "user") {
                                            Box(
                                                modifier = Modifier.widthIn(
                                                    min = 120.dp,
                                                    max = if (screenSizeState == ScreenSizeState.EXPANDED) {
                                                        550.dp
                                                    } else {
                                                        270.dp
                                                    }
                                                ).padding(vertical = 10.dp)
                                                    .background(
                                                        BlackBackgroundShade,
                                                        RoundedCornerShape(20.dp)
                                                    )
                                                    .clip(RoundedCornerShape(20.dp)),
                                            ) {
                                                Text(
                                                    text = chatsList[index].second,
                                                    textAlign = TextAlign.Start,
                                                    style = MaterialTheme.typography.bodyLarge,
                                                    color = Color.White,
                                                    modifier = Modifier.padding(20.dp)
                                                )
                                            }
                                        }
                                        // MODEL
                                        else {
                                            Box(
                                                modifier = Modifier.fillMaxWidth()
                                                    .padding(vertical = 10.dp)
                                            ) {
                                                Text(
                                                    text = chatsList[index].second,
                                                    textAlign = TextAlign.Start,
                                                    style = MaterialTheme.typography.bodyLarge,
                                                    color = Color.White,
                                                    modifier = Modifier.padding(bottom = 20.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                                // ROTATING LOTUS
                                if (currentResponseState) {
                                    item {
                                        Box(
                                            modifier = Modifier.fillMaxWidth()
                                                .padding(vertical = 10.dp)
                                        ) {
                                            Image(
                                                painter = painterResource(Res.drawable.response_loader_icon),
                                                contentDescription = "Talk With Curiosity",
                                                modifier = Modifier.size(25.dp)
                                                    .graphicsLayer {
                                                        rotationZ = rotation
                                                    }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        // MESSAGE TEXT FIELD
                        Box(
                            Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .fillMaxWidth()
                                    .height(108.dp)
                                    .background(
                                        brush = Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                Color.Black.copy(alpha = 0.6f)
                                            )
                                        )
                                    )
                            )
                            BasicTextField(
                                state = messageFieldState,
                                enabled = !currentResponseState,
                                textStyle = MaterialTheme.typography.bodyLarge.copy(
                                    color = Color.White
                                ),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                onKeyboardAction = {
                                    if (messageFieldState.text.trim().isNotEmpty()) {
                                        viewModel.onMessageQuerySent(messageFieldState.text.toString())
                                        messageFieldState.clearText()
                                    }
                                    keyboardController?.hide()
                                },
                                modifier = Modifier.align(Alignment.BottomCenter)
                                    .padding(vertical = 20.dp).imePadding()
                                    .fillMaxWidth(
                                        when (screenSizeState) {
                                            ScreenSizeState.COMPACT -> 0.9f
                                            ScreenSizeState.MEDIUM -> 0.7f
                                            ScreenSizeState.EXPANDED -> 0.6f
                                        }
                                    )
                                    .border(
                                        width = 0.75.dp,
                                        color = Color.DarkGray,
                                        shape = RoundedCornerShape(
                                            size = if (lineCount <= 1) 40.dp else 15.dp
                                        )
                                    )
                                    .onPreviewKeyEvent { event ->
                                        if (
                                            event.type == KeyEventType.KeyDown &&
                                            (event.key == Key.Enter || event.key == Key.NumPadEnter) &&
                                            !event.isShiftPressed
                                        ) {
                                            val query = messageFieldState.text.toString()

                                            if (query.trim().isNotEmpty()) {
                                                viewModel.onMessageQuerySent(query)
                                                messageFieldState.clearText()
                                            }

                                            true
                                        } else if (
                                            event.type == KeyEventType.KeyDown &&
                                            (event.key == Key.Enter || event.key == Key.NumPadEnter) &&
                                            event.isShiftPressed
                                        ) {
                                            messageFieldState.edit {
                                                insert(selection.min, "\n")
                                            }
                                            true
                                        } else {
                                            false
                                        }
                                    },
                                lineLimits = TextFieldLineLimits.MultiLine(),
                                onTextLayout = { result ->
                                    val layoutResult = result()
                                    layoutResult?.let {
                                        lineCount = it.lineCount
                                    }
                                },
                                cursorBrush = SolidColor(Color.White),
                                decorator = OutlinedTextFieldDefaults.decorator(
                                    trailingIcon = {
                                        androidx.compose.animation.AnimatedVisibility(
                                            visible = messageFieldState.text.trim().isEmpty(),
                                            enter = scaleIn(
                                                initialScale = 0.3f,
                                                animationSpec = spring(
                                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                                    stiffness = Spring.StiffnessLow
                                                )
                                            ) + fadeIn(),
                                            exit = scaleOut(
                                                targetScale = 0.3f,
                                                animationSpec = spring(
                                                    dampingRatio = Spring.DampingRatioNoBouncy,
                                                    stiffness = Spring.StiffnessMedium
                                                )
                                            ) + fadeOut()
                                        ) {
                                            InactiveTextFieldSearchIcon()
                                        }
                                        androidx.compose.animation.AnimatedVisibility(
                                            visible = messageFieldState.text.trim().isNotEmpty(),
                                            enter = scaleIn(
                                                initialScale = 0.3f,
                                                animationSpec = spring(
                                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                                    stiffness = Spring.StiffnessLow
                                                )
                                            ) + fadeIn(),
                                            exit = scaleOut(
                                                targetScale = 0.3f,
                                                animationSpec = spring(
                                                    dampingRatio = Spring.DampingRatioNoBouncy,
                                                    stiffness = Spring.StiffnessMedium
                                                )
                                            ) + fadeOut()
                                        ) {
                                            ActiveTextFieldMessageSendIcon(
                                                keyboardController,
                                                viewModel::onMessageQuerySent,
                                                messageFieldState.text.toString(),
                                                messageFieldState
                                            )
                                        }
                                    },
                                    state = messageFieldState,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = BlackBackgroundShade,
                                        unfocusedContainerColor = BlackBackgroundShade,
                                        focusedBorderColor = Color.Transparent,
                                        unfocusedBorderColor = Color.Transparent,
                                        disabledBorderColor = Color.Transparent,
                                        errorBorderColor = Color.Transparent
                                    ),
                                    placeholder = {
                                        Text(
                                            text = when (currentResponseState) {
                                                false -> "Be Curious"
                                                true -> "Discovering..."
                                            },
                                            color = Color.Gray,
                                            style = MaterialTheme.typography.bodyLarge

                                        )
                                    },
                                    contentPadding = PaddingValues(
                                        start = 18.dp,
                                        end = 18.dp,
                                        top = 10.dp,
                                        bottom = 10.dp
                                    ),
                                    enabled = true,
                                    lineLimits = TextFieldLineLimits.MultiLine(),
                                    outputTransformation = null,
                                    interactionSource = interactionSource,
                                    container = {
                                        OutlinedTextFieldDefaults.Container(
                                            enabled = true,
                                            isError = false,
                                            interactionSource = interactionSource,
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedContainerColor = BlackBackgroundShade,
                                                unfocusedContainerColor = BlackBackgroundShade,
                                                focusedBorderColor = Color.Transparent,
                                                unfocusedBorderColor = Color.Transparent,
                                                disabledBorderColor = Color.Transparent,
                                                errorBorderColor = Color.Transparent,
                                            ),
                                            shape = RoundedCornerShape(
                                                size = if (lineCount == 1) 40.dp else 15.dp
                                            )
                                        )
                                    }
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
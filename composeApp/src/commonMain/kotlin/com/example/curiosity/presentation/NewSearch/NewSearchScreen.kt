package com.example.curiosity.presentation.NewSearch

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.insert
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.curiosity.core.navigation.SearchAndDiscoveryChatScreen
import com.example.curiosity.presentation.SearchStates.ActiveTextFieldSearchIcon
import com.example.curiosity.presentation.SearchStates.InactiveTextFieldSearchIcon
import com.example.curiosity.presentation.SearchStates.SearchCuriositySuggestion
import com.example.curiosity.presentation.SearchStates.bottomAnimatedTaperedGradientBorder
import com.example.curiosity.presentation.WebView.DeviceBasedWebView
import com.example.curiosity.theme.BlackBackgroundShade
import com.example.curiosity.theme.CuriosityTheme
import com.example.curiosity.theme.ElectricBlue
import com.example.curiosity.theme.GoldenYellow
import com.example.curiosity.theme.VividMagenta
import curiosity.composeapp.generated.resources.Res
import curiosity.composeapp.generated.resources.curiosity_icon
import curiosity.composeapp.generated.resources.curiosity_recentsearches_icon
import curiosity.composeapp.generated.resources.response_loader_icon
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

@Composable
fun NewSearchScreen(
    viewModel: NewSearchScreenViewModel,
    drawerState: DrawerState,
    scope: CoroutineScope,
    navigator: NavHostController,
    currentUserSessionDataViewModel: CurrentUserSessionDataViewModel,
    searchId: Int
){
    val screenWidth = viewModel.widthState.collectAsState()

    val screenSizeState = ScreenSizeState.fromWidth(screenWidth.value)

    val searchFieldState = rememberTextFieldState()

    val interactionSource = remember { MutableInteractionSource() }

    val keyboardController = LocalSoftwareKeyboardController.current

    val searchHeadingText = viewModel.searchHeadingTextState.collectAsState()

    val currentSearchState = viewModel.currentSearchingState.collectAsState()

    val currentSearchQuery = viewModel.currentSearchQueryState.collectAsState()

    val currentCuriousQuestion by viewModel.searchCurrentCuriousQuestionState.collectAsState()

    val currentCuriousQuestionTopic by viewModel.searchCurrentCuriousQuestionTopicHeadingState.collectAsState()

    val currentCuriousQuestionBoxState by viewModel.searchCurrentCuriousQuestionBoxState.collectAsState()

    val webPageLoading by viewModel.webPageLoadingStatus.collectAsState()

    val showWebView by viewModel.showWebViewState.collectAsState()

    val currentUrl by viewModel.processedUrlState.collectAsState()


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
                        modifier = when(currentSearchState.value){
                            true -> Modifier.bottomAnimatedTaperedGradientBorder(
                                baseColors = colors
                            )
                            false -> Modifier
                        },
                        contentAlignment = Alignment.Center
                    ){
                        Row(
                            modifier = Modifier.padding(
                                vertical = when(screenSizeState){
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
                            // RECENT SEARCHES
                            if (screenSizeState == ScreenSizeState.COMPACT){
                                Box(
                                    modifier = Modifier.size(45.dp)
                                        .clip(CircleShape)
                                        .background(BlackBackgroundShade)
                                        .border(
                                            width = 0.75.dp,
                                            color = Color.DarkGray,
                                            shape = CircleShape
                                        )
                                        .clickable{
                                            // RECENT SEARCHES OPEN DRAWER
                                            scope.launch {
                                                if (drawerState.isClosed){
                                                    drawerState.open()
                                                }
                                            }
                                        }
                                        .pointerHoverIcon(PointerIcon.Default),
                                    contentAlignment = Alignment.Center
                                ){
                                    Icon(
                                        painter = painterResource(Res.drawable.curiosity_recentsearches_icon),
                                        tint = Color.White,
                                        contentDescription = "Recent Searches Icon",
                                        modifier = Modifier.padding(10.dp).fillMaxSize()
                                    )
                                }
                            }
                            else{
                                // Empty Box to make Header in centre
                                Box(
                                    modifier = Modifier.size(45.dp)
                                )
                            }
                            // CURRENT SEARCH HEADER
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
                            ){
                                Text(
                                    text = searchHeadingText.value,
                                    style = when (screenSizeState){
                                        ScreenSizeState.COMPACT -> MaterialTheme.typography.bodyLarge
                                        ScreenSizeState.MEDIUM -> MaterialTheme.typography.bodyLarge
                                        ScreenSizeState.EXPANDED -> MaterialTheme.typography.titleLarge
                                    },
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = TextAlign.Center,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                                )
                            }
                            // TALK WITH CURIOSITY
                            Box(
                                modifier = Modifier.size(45.dp)
                                    .clip(CircleShape)
                                    .background(BlackBackgroundShade)
                                    .border(
                                        width = 0.75.dp,
                                        color = Color.DarkGray,
                                        shape = CircleShape
                                    )
                                    .clickable{
                                        // OPEN CHAT PANEL WITH CURIOSITY LOGIC
                                        viewModel.disableCurrentCuriousQuestionBox()
                                        navigator.navigate(SearchAndDiscoveryChatScreen(
                                            searchId = searchId,
                                            questionTopic = currentCuriousQuestionTopic,
                                            questionQuery = currentCuriousQuestion
                                        ))
                                    }
                                    .pointerHoverIcon(PointerIcon.Default),
                                contentAlignment = Alignment.Center
                            ){
                                Image(
                                    painter = painterResource(Res.drawable.response_loader_icon),
                                    contentDescription = "Talk With Curiosity",
                                    modifier = Modifier.padding(8.dp).fillMaxSize().clip(CircleShape)
                                )
                            }
                        }
                    }
                    // SEARCH RESULTS AREA AND BOTTOM SEARCH FIELD
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceEvenly,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            when((currentSearchState.value || webPageLoading) && !showWebView){
                                true -> {
                                    Box(
                                        modifier = Modifier.fillMaxWidth(
                                            when(screenSizeState){
                                                ScreenSizeState.COMPACT -> 0.75f
                                                ScreenSizeState.MEDIUM -> 0.75f
                                                ScreenSizeState.EXPANDED -> 0.4f
                                            }
                                        )
                                            .offset(y = -(60.dp)),
                                        contentAlignment = Alignment.Center
                                    ){
                                        androidx.compose.animation.AnimatedVisibility(
                                            visible = currentSearchState.value,
                                            enter = fadeIn(),
                                            exit = fadeOut()
                                        ){
                                            Text(
                                                text = currentSearchQuery.value,
                                                maxLines = 5,
                                                overflow = TextOverflow.Ellipsis,
                                                textAlign = TextAlign.Center,
                                                style = MaterialTheme.typography.titleLarge.copy(
                                                    brush = gradientBrush
                                                ),
                                                modifier = Modifier.padding(
                                                    horizontal = 10.dp,
                                                    vertical = 10.dp
                                                )
                                            )
                                        }
                                    }
                                }
                                false -> {
                                    // SEARCH RESULTS AREA
                                    Box(
                                        modifier = Modifier.fillMaxSize()
                                    ){
                                        Column(
                                            Modifier.fillMaxSize()
                                        ) {
                                            if (currentCuriousQuestionBoxState){
                                                Box(
                                                    Modifier.align(Alignment.End)
                                                ){
                                                    SearchCuriositySuggestion(
                                                        screenSizeState,
                                                        gradientBrush,
                                                        searchId,
                                                        currentCuriousQuestion,
                                                        currentCuriousQuestionTopic,
                                                        navigator,
                                                        viewModel::disableCurrentCuriousQuestionBox
                                                    )
                                                }
                                            }
                                            if (showWebView){
                                                Box(
                                                    Modifier.fillMaxSize()
                                                ){
                                                    DeviceBasedWebView(
                                                        screenSizeState = screenSizeState,
                                                        pageUrl = currentUrl,
                                                        viewModel::onWebPageLoaded,
                                                        viewModel::disableWebView
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        // SEARCH TEXT FIELD
                        Box(
                            Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
                            contentAlignment = Alignment.BottomCenter
                        ){
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
                                state = searchFieldState,
                                enabled = !currentSearchState.value,
                                textStyle = MaterialTheme.typography.bodyLarge.copy(
                                    color = Color.White
                                ),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                onKeyboardAction = {
                                    val query = searchFieldState.text.toString()
                                    if (searchFieldState.text.trim().isNotEmpty()){
                                        viewModel.onSearchQueryChange(searchFieldState.text.toString())
                                        searchFieldState.clearText()
                                        viewModel.onSearchStarted(query)
                                    }
                                    keyboardController?.hide()
                                },
                                modifier = Modifier.align(Alignment.BottomCenter).padding(vertical = 20.dp).imePadding()
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
                                            val query = searchFieldState.text.toString()

                                            if (query.trim().isNotEmpty()){
                                                viewModel.onSearchQueryChange(query)
                                                viewModel.onSearchStarted(query)
                                                searchFieldState.clearText()
                                            }

                                            true
                                        }
                                        else if (
                                            event.type == KeyEventType.KeyDown &&
                                            (event.key == Key.Enter || event.key == Key.NumPadEnter) &&
                                            event.isShiftPressed
                                        ) {
                                            searchFieldState.edit {
                                                insert(selection.min, "\n")
                                            }
                                            true
                                        }
                                        else {
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
                                            visible = searchFieldState.text.trim().isEmpty(),
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
                                            visible = searchFieldState.text.trim().isNotEmpty(),
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
                                            ActiveTextFieldSearchIcon(
                                                keyboardController,
                                                viewModel::onSearchStarted,
                                                viewModel::onSearchQueryChange,
                                                searchFieldState.text.toString(),
                                                searchFieldState
                                            )
                                        }
                                    },
                                    state = searchFieldState,
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
                                            text = when(currentSearchState.value){
                                                false -> "Be Curious"
                                                true -> "Searching..."
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
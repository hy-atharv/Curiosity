package com.example.curiosity.presentation.Home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.curiosity.theme.CuriosityTheme
import curiosity.composeapp.generated.resources.Res
import curiosity.composeapp.generated.resources.curiosity_screen_art
import org.jetbrains.compose.resources.painterResource
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.navigation.NavHostController
import com.example.curiosity.core.models.CurrentUserSessionDataViewModel
import com.example.curiosity.core.models.Rng
import com.example.curiosity.core.models.ScreenSizeState
import com.example.curiosity.core.models.SearchData
import com.example.curiosity.presentation.SearchStates.ActiveTextFieldSearchIcon
import com.example.curiosity.presentation.SearchStates.InactiveTextFieldSearchIcon
import com.example.curiosity.theme.BlackBackgroundShade
import com.example.curiosity.theme.ElectricBlue
import com.example.curiosity.theme.GoldenYellow
import com.example.curiosity.theme.VividMagenta
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel,
    currentUserSessionDataViewModel: CurrentUserSessionDataViewModel,
    drawerState: DrawerState,
    scope: CoroutineScope,
    navigator: NavHostController
){
    val screenWidth = viewModel.widthState.collectAsState()

    val screenSizeState = ScreenSizeState.fromWidth(screenWidth.value)

    val searchFieldState = rememberTextFieldState()

    val interactionSource = remember { MutableInteractionSource() }

    val keyboardController = LocalSoftwareKeyboardController.current

    var lineCount by remember { mutableStateOf(1) }

    val fullViewState = viewModel.currentFullScreenState.collectAsState()

    val colors = listOf<Color>(
        GoldenYellow,
        ElectricBlue,
        VividMagenta
    )

    val textFieldBrush = remember {
        Brush.linearGradient(colors)
    }

    SharedTransitionLayout {
        AnimatedContent(
            targetState = fullViewState.value,
            label = "Home Screen Search Transition"
        ){ show ->
            if (show){
                HomeDrawer(
                    viewModel = viewModel,
                    currentUserSessionDataViewModel = currentUserSessionDataViewModel,
                    animatedVisibilityScope = this@AnimatedContent,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    drawerState = drawerState,
                    scope = scope,
                    navigator = navigator
                )
            }
            else{
                val animatedVisibilityScope = this@AnimatedContent
                val sharedTransitionScope = this@SharedTransitionLayout
                CuriosityTheme {
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color = Color.Black)
                            .windowInsetsPadding(WindowInsets.systemBars),
                        color = Color.Black
                    ){
                        BoxWithConstraints(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            viewModel.onWidthChange(maxWidth.value.toInt())
                            Column(
                                modifier = Modifier.fillMaxSize().offset(y=-(60).dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                val lotusSize = when (screenSizeState) {
                                    ScreenSizeState.COMPACT -> screenWidth.value * 0.55f
                                    ScreenSizeState.MEDIUM -> screenWidth.value * 0.45f
                                    ScreenSizeState.EXPANDED -> screenWidth.value * 0.25f
                                }

                                Box(
                                    modifier = Modifier.size(lotusSize.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    GloomingLotus(screenWidth.value, animatedVisibilityScope, sharedTransitionScope)
                                }
                                Text(
                                    text = "Curiosity",
                                    color = Color.White,
                                    style = when (screenSizeState) {
                                        ScreenSizeState.COMPACT -> MaterialTheme.typography.headlineLarge
                                        ScreenSizeState.MEDIUM -> MaterialTheme.typography.headlineLarge
                                        ScreenSizeState.EXPANDED -> MaterialTheme.typography.displayMedium
                                    },
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "Search That Thinks With You",
                                    color = Color.Gray,
                                    style = when (screenSizeState) {
                                        ScreenSizeState.COMPACT -> MaterialTheme.typography.titleLarge
                                        ScreenSizeState.MEDIUM -> MaterialTheme.typography.titleLarge
                                        ScreenSizeState.EXPANDED -> MaterialTheme.typography.headlineLarge
                                    },
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(10.dp)
                                )
                                BasicTextField(
                                    state = searchFieldState,
                                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                                        brush = textFieldBrush
                                    ),
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                    onKeyboardAction = {
                                        val query = searchFieldState.text.toString()
                                        if (searchFieldState.text.trim().isNotEmpty()){
                                            viewModel.onSearchQueryChange(searchFieldState.text.toString())
                                            searchFieldState.clearText()
                                            viewModel.onSearchAndNavigateToFullView(query)
                                            currentUserSessionDataViewModel.insertSearchDataToSearchDataMap(
                                                SearchData(
                                                    searchId = Rng.generateSearchId(),
                                                    searchHeading = "New Search",
                                                    searchInitialQuery = query,
                                                    searchAllQueriesAndResults = emptyList()
                                                )
                                            )
                                        }
                                        keyboardController?.hide()
                                    },
                                    modifier = Modifier.padding(top = 20.dp).imePadding()
                                        .fillMaxWidth(
                                            when (screenSizeState) {
                                                ScreenSizeState.COMPACT -> 0.85f
                                                ScreenSizeState.MEDIUM -> 0.65f
                                                ScreenSizeState.EXPANDED -> 0.45f
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
                                                    viewModel.onSearchAndNavigateToFullView(query)
                                                    searchFieldState.clearText()
                                                    currentUserSessionDataViewModel.insertSearchDataToSearchDataMap(
                                                        SearchData(
                                                            searchId = Rng.generateSearchId(),
                                                            searchHeading = "New Search",
                                                            searchInitialQuery = query,
                                                            searchAllQueriesAndResults = emptyList()
                                                        )
                                                    )
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
                                                    viewModel::onSearchAndNavigateToFullView,
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
                                                text = "Be Curious",
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
}


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun GloomingLotus(
    screenWidth: Int,
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope
){
    val screenSizeState = ScreenSizeState.fromWidth(screenWidth)

    val infiniteTransition = rememberInfiniteTransition(label = "curiosity_image_anim")

    // Rotation: 0 → 360 degrees
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 12000,
                easing = LinearEasing
            )
        ),
        label = "rotation"
    )

    // Zoom: 0.95 → 1.05 → 0.95
    val scale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 3000,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val colorCycle = listOf<Color>(
        GoldenYellow,
        ElectricBlue,
        VividMagenta
    )

    val colorIndex = ((rotation / 120f).toInt()) % colorCycle.size
    val currentColor = colorCycle[colorIndex]

    val animatedColor by animateColorAsState(
        targetValue = currentColor,
        animationSpec = tween(
            durationMillis = 600,
            easing = FastOutSlowInEasing
        ),
        label = "color"
    )

    with(sharedTransitionScope){
        Image(
            painter = painterResource(Res.drawable.curiosity_screen_art),
            contentDescription = "Curiosity Search Engine",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .sharedElement(
                    rememberSharedContentState(key = "curiosity_logo"),
                    animatedVisibilityScope = animatedVisibilityScope
                )
                .padding(bottom = when(screenSizeState) {
                    ScreenSizeState.COMPACT -> 5.dp
                    ScreenSizeState.MEDIUM -> 15.dp
                    ScreenSizeState.EXPANDED -> 25.dp
                }
                )
                .aspectRatio(1f)
                .dropShadow(
                    shape = CircleShape,
                    shadow = Shadow(
                        radius = 100.dp,
                        spread = 20.dp,
                        color = animatedColor,
                        alpha = 0.2f
                    )
                )
                .graphicsLayer {
                    rotationZ = rotation
                    scaleX = scale
                    scaleY = scale
                    transformOrigin = TransformOrigin.Center
                }
        )
    }
}








//OutlinedTextField(
//state = searchFieldState,
//textStyle = MaterialTheme.typography.bodyLarge.copy(
//brush = textFieldBrush
//),
//placeholder = {
//    Text(
//        text = "Be Curious",
//        color = Color.Gray,
//        style = MaterialTheme.typography.bodyLarge
//
//    )
//},
//shape = RoundedCornerShape(40.dp),
//colors = OutlinedTextFieldDefaults.colors(
//focusedContainerColor = BlackBackgroundShade,
//unfocusedContainerColor = BlackBackgroundShade,
//focusedBorderColor = Color.Transparent,
//unfocusedBorderColor = Color.Transparent,
//disabledBorderColor = Color.Transparent,
//errorBorderColor = Color.Transparent,
//cursorColor = Color.White,
//),
//lineLimits = TextFieldLineLimits.MultiLine(),
//modifier = Modifier.padding(top = 20.dp)
//.fillMaxWidth(0.80f)
//.border(
//width = 0.75.dp,
//color = Color.DarkGray,
//shape = RoundedCornerShape(40.dp)
//),
//)


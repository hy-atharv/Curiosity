package com.example.curiosity.presentation.WebView

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.curiosity.core.models.ScreenSizeState
import com.example.curiosity.presentation.SearchStates.topAnimatedTaperedGradientBorder
import com.example.curiosity.theme.ElectricBlue
import com.example.curiosity.theme.GoldenYellow
import com.example.curiosity.theme.VividMagenta
import com.multiplatform.webview.util.KLogSeverity
import com.multiplatform.webview.web.LoadingState
import com.multiplatform.webview.web.WebContent
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewNavigator
import com.multiplatform.webview.web.rememberWebViewState
import curiosity.composeapp.generated.resources.Res
import curiosity.composeapp.generated.resources.back
import curiosity.composeapp.generated.resources.close_new
import kotlinx.coroutines.flow.distinctUntilChanged
import org.jetbrains.compose.resources.painterResource

@Composable
fun DeviceBasedWebView(
    screenSizeState: ScreenSizeState,
    pageUrl: String,
    onWebPageLoaded: () -> Unit,
    disableWebView: () -> Unit
){

    val colors = listOf<Color>(
        GoldenYellow,
        ElectricBlue,
        VividMagenta
    )

    val gradientBrush = remember {
        Brush.linearGradient(colors)
    }

    val pageUrlState = rememberWebViewState(url = pageUrl)

    pageUrlState.viewState

    val webViewNavigator = rememberWebViewNavigator()

    val pageDomain = pageUrlState.lastLoadedUrl?.substringAfter("//")
        ?.substringBefore("/")
        ?.removePrefix("www.")
        ?:pageUrl.substringAfter("//")
            .substringBefore("/")
            .removePrefix("www.");

    LaunchedEffect(Unit) {
        pageUrlState.webSettings.apply {
            customUserAgentString = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) HeadlessChrome/126.0.0.0 Safari/537.36"
            logSeverity = KLogSeverity.Debug
        }
        pageUrlState.webSettings.desktopWebSettings.apply {
            disablePopupWindows = true
        }
        snapshotFlow { pageUrlState.loadingState }
            .distinctUntilChanged()
            .collect { state ->
                if (state is LoadingState.Finished) {
                    onWebPageLoaded()
                }
            }
    }

    val hasMainFrameError = pageUrlState.errorsForCurrentRequest.any { it.isFromMainFrame }

    // LOADING BAR
    if (pageUrlState.loadingState is LoadingState.Loading) {
        Box(
            modifier = Modifier.fillMaxSize().topAnimatedTaperedGradientBorder(colors).zIndex(7f)
        )
    }
    // WEB VIEW
    Column(
        modifier = Modifier.fillMaxSize().background(Color.Black),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        if (screenSizeState == ScreenSizeState.EXPANDED){
            Row(
                modifier = Modifier.fillMaxWidth(0.9f),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Web View Navigation Bar
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ){
                    Icon(
                        painter = painterResource(Res.drawable.back),
                        contentDescription = "Prev Page",
                        tint = if (webViewNavigator.canGoBack) Color.White else Color.Gray,
                        modifier = Modifier.padding(start = 20.dp, end = 10.dp).size(18.dp)
                            .clickable(
                                enabled = true,
                                onClick = {
                                    // PREV PAGE LOGIC
                                    webViewNavigator.navigateBack()
                                }
                            )
                    )
                    Icon(
                        painter = painterResource(Res.drawable.back),
                        contentDescription = "Next Page",
                        tint = if (webViewNavigator.canGoForward) Color.White else Color.Gray,
                        modifier = Modifier.padding(end = 20.dp).size(18.dp)
                            .graphicsLayer{
                                scaleX = -1f
                            }
                            .clickable(
                                enabled = true,
                                onClick = {
                                    // NEXT PAGE LOGIC
                                    webViewNavigator.navigateForward()
                                }
                            )
                    )
                }
                // Domain Name
                Text(
                    text = pageDomain,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        brush = gradientBrush
                    ),
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(vertical = 15.dp)
                )
                // Close Page Button
                Icon(
                    painter = painterResource(Res.drawable.close_new),
                    contentDescription = "Close Page",
                    tint = Color.White,
                    modifier = Modifier.padding(horizontal = 20.dp).size(20.dp)
                        .clickable{
                            // CLOSE PAGE LOGIC
                            disableWebView()
                        }
                )
            }
        }
        else{
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Web View Navigation Buttons
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.back),
                        contentDescription = "Prev Page",
                        tint = if (webViewNavigator.canGoBack) Color.White else Color.Gray,
                        modifier = Modifier.padding(start = 20.dp, end = 10.dp).size(13.dp)
                            .clickable(
                                enabled = true,
                                onClick = {
                                    // PREV PAGE LOGIC
                                    webViewNavigator.navigateBack()
                                }
                            )
                    )
                    Icon(
                        painter = painterResource(Res.drawable.back),
                        contentDescription = "Next Page",
                        tint = if (webViewNavigator.canGoForward) Color.White else Color.Gray,
                        modifier = Modifier.padding(end = 20.dp).size(13.dp)
                            .graphicsLayer {
                                scaleX = -1f
                            }
                            .clickable(
                                enabled = true,
                                onClick = {
                                    // NEXT PAGE LOGIC
                                    webViewNavigator.navigateForward()
                                }
                            )
                    )
                }
                // Domain Name
                Text(
                    text = pageDomain,
                    style = MaterialTheme.typography.labelLarge.copy(
                        brush = gradientBrush,
                        fontWeight = FontWeight.Normal
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 10.dp)
                )
                // Close Page Button
                Icon(
                    painter = painterResource(Res.drawable.close_new),
                    contentDescription = "Close Page",
                    tint = Color.White,
                    modifier = Modifier.padding(horizontal = 20.dp).size(15.dp)
                        .clickable {
                            // CLOSE PAGE LOGIC
                            disableWebView()
                        }
                )
            }
        }
        if (!hasMainFrameError) {
            WebView(
                state = pageUrlState,
                navigator = webViewNavigator,
                modifier = when(screenSizeState) {
                    ScreenSizeState.COMPACT -> Modifier.fillMaxSize()
                    ScreenSizeState.MEDIUM -> Modifier.fillMaxSize()
                    ScreenSizeState.EXPANDED -> Modifier.fillMaxSize(0.9f)
                }.zIndex(2f)
            )
        } else {
            Text(
                text = pageUrlState.errorsForCurrentRequest.find { it.isFromMainFrame }?.description ?: "An unknown error occured",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color(0xFFED7F7D),
                    fontWeight = FontWeight.SemiBold
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 200.dp)
            )
        }
    }
}
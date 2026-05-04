package com.example.curiosity.presentation.RecentSearch

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.curiosity.core.models.CurrentUserSessionDataViewModel
import com.example.curiosity.core.models.Rng
import com.example.curiosity.core.models.ScreenSizeState
import com.example.curiosity.core.models.SearchData
import com.example.curiosity.core.navigation.NewSearchScreen
import com.example.curiosity.core.navigation.RecentSearchNavigation
import com.example.curiosity.presentation.RecentSearchItem
import curiosity.composeapp.generated.resources.Res
import curiosity.composeapp.generated.resources.curiosity_startnewsearch
import kotlinx.coroutines.CoroutineScope
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun RecentSearchDrawer(
    viewModel: RecentSearchScreenViewModel = koinViewModel<RecentSearchScreenViewModel>(),
    currentUserSessionDataViewModel: CurrentUserSessionDataViewModel,
    drawerState: DrawerState,
    scope: CoroutineScope,
    navigator: NavHostController,
    searchId: Int
){
    val screenWidth = viewModel.widthState.collectAsState()

    val screenSizeState = ScreenSizeState.fromWidth(screenWidth.value)

    val currentSearchState = viewModel.currentSearchingState.collectAsState()

    val recentSearchesList = currentUserSessionDataViewModel.recentSearches.collectAsState()


    when(screenSizeState){
        ScreenSizeState.COMPACT -> {
            RecentSearchNavigation(
                viewModel = viewModel,
                drawerState = drawerState,
                scope = scope,
                mainNavigator = navigator,
                currentUserSessionDataViewModel = currentUserSessionDataViewModel,
                searchId = searchId
            )
        }
        else -> {
            PermanentNavigationDrawer(
                drawerContent = {
                    PermanentDrawerSheet(
                        drawerContainerColor = Color(0xFF0D0D0D),
                        drawerShape = RectangleShape,
                        modifier = Modifier.drawWithContent {
                            drawContent() // draw drawer first

                            val strokeWidth = 0.25.dp.toPx()
                            val x = size.width - strokeWidth / 2f

                            drawLine(
                                color = Color.DarkGray,
                                start = Offset(x, 0f),
                                end = Offset(x, size.height),
                                strokeWidth = strokeWidth
                            )
                        }
                    ) {
                        Column(
                            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 30.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 10.dp)
                                    .clickable {
                                        // LOGIC TO START NEW SEARCH
                                        if (!currentSearchState.value){
                                            val id = Rng.generateSearchId()
                                            currentUserSessionDataViewModel.insertSearchDataToSearchDataMap(
                                                SearchData(
                                                    searchId = id,
                                                    searchHeading = "New Search",
                                                    searchInitialQuery = "",
                                                    searchAllQueriesAndResults = emptyList()
                                                )
                                            )
                                            navigator.navigate(NewSearchScreen(id))
                                        }
                                    }
                                    .pointerHoverIcon(PointerIcon.Hand)
                            ) {
                                Icon(
                                    painter = painterResource(Res.drawable.curiosity_startnewsearch),
                                    tint = Color.Gray,
                                    contentDescription = "Start New Search",
                                    modifier = Modifier.padding(end = 10.dp).size(25.dp)
                                )
                                Text(
                                    text = "New search",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White,
                                    textAlign = TextAlign.Center,
                                )
                            }
                            Text(
                                text = "Recent",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 10.dp)
                            )
                            recentSearchesList.value.asReversed().forEach { searchItem ->
                                RecentSearchItem(
                                    sessionDataViewModel = currentUserSessionDataViewModel,
                                    searchId = searchItem.first,
                                    drawerState = drawerState,
                                    scope = scope,
                                    navigator = navigator
                                )
                            }
                        }
                    }
                }
            ){
                RecentSearchNavigation(
                    viewModel = viewModel,
                    drawerState = drawerState,
                    scope = scope,
                    mainNavigator = navigator,
                    currentUserSessionDataViewModel = currentUserSessionDataViewModel,
                    searchId = searchId
                )
            }
        }
    }
}
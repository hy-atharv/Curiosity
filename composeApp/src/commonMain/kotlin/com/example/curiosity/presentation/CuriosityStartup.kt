package com.example.curiosity.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.curiosity.core.models.CurrentUserSessionDataViewModel
import com.example.curiosity.core.models.Rng
import com.example.curiosity.core.models.ScreenSizeState
import com.example.curiosity.core.models.SearchData
import com.example.curiosity.core.navigation.HomeScreen
import com.example.curiosity.core.navigation.MainNavigation
import com.example.curiosity.core.navigation.NewSearchScreen
import com.example.curiosity.core.navigation.RecentSearchScreen
import com.example.curiosity.presentation.Home.HomeScreenViewModel
import com.example.curiosity.presentation.NewSearch.NewSearchScreenViewModel
import curiosity.composeapp.generated.resources.Res
import curiosity.composeapp.generated.resources.curiosity_startnewsearch
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CuriosityApp (
    homeViewModel: HomeScreenViewModel = koinViewModel<HomeScreenViewModel>(),
    newSearchViewModel: NewSearchScreenViewModel = koinViewModel<NewSearchScreenViewModel>(),
    currentSessionDataViewModel: CurrentUserSessionDataViewModel = koinViewModel<CurrentUserSessionDataViewModel>()
){
    val screenWidth = homeViewModel.widthState.collectAsState()
    val screenSizeState = ScreenSizeState.fromWidth(screenWidth.value)

    val navigator = rememberNavController()

    val navBackStackEntry = navigator.currentBackStackEntryAsState()

    val currentNewSearchStartedState = newSearchViewModel.currentNewSearchStartedState.collectAsState()

    val currentNewSearchState = newSearchViewModel.currentSearchingState.collectAsState()

    val currentHomeSearchState = homeViewModel.currentSearchingState.collectAsState()


    val recentSearchesList = currentSessionDataViewModel.recentSearches.collectAsState()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val renameSearchPopUpState by currentSessionDataViewModel.renamePopUpState.collectAsState()


    when(screenSizeState){
        ScreenSizeState.COMPACT -> {
            ModalNavigationDrawer(
                drawerState = drawerState,
                scrimColor = Color.DarkGray.copy(alpha = 0.5f),
                gesturesEnabled = drawerState.isOpen,
                drawerContent = {
                    ModalDrawerSheet(
                        drawerContainerColor = Color(0xFF0D0D0D),
                        drawerShape = RectangleShape
                    ) {
                        Column(
                            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 30.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 10.dp)
                                    .clickable{
                                        // LOGIC TO START NEW SEARCH
                                        scope.launch {
                                            if (drawerState.isOpen){
                                                drawerState.close()
                                            }
                                        }
                                        if (navBackStackEntry.value?.destination?.hasRoute<HomeScreen>()==true){
                                            if (!currentHomeSearchState.value) {
                                                val id = Rng.generateSearchId()
                                                currentSessionDataViewModel.insertSearchDataToSearchDataMap(
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
                                        else if (navBackStackEntry.value?.destination?.hasRoute<NewSearchScreen>()==true){
                                            if (currentNewSearchStartedState.value && !currentNewSearchState.value) {
                                                val id = Rng.generateSearchId()
                                                currentSessionDataViewModel.insertSearchDataToSearchDataMap(
                                                    SearchData(
                                                        searchId = id,
                                                        searchHeading = "New Search",
                                                        searchInitialQuery = "",
                                                        searchAllQueriesAndResults = emptyList()
                                                    )
                                                )
                                                newSearchViewModel.onViewModelResetState()
                                                navigator.navigate(NewSearchScreen(id))
                                            }
                                        }
                                        else if (navBackStackEntry.value?.destination?.hasRoute<RecentSearchScreen>()==true){
                                            val id = Rng.generateSearchId()
                                            currentSessionDataViewModel.insertSearchDataToSearchDataMap(
                                                SearchData(
                                                    searchId = Rng.generateSearchId(),
                                                    searchHeading = "New Search",
                                                    searchInitialQuery = "",
                                                    searchAllQueriesAndResults = emptyList()
                                                )
                                            )
                                            navigator.navigate(NewSearchScreen(id))
                                        }
                                    }
                                    .pointerHoverIcon(PointerIcon.Hand)
                            ){
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
                                    sessionDataViewModel = currentSessionDataViewModel,
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
                MainNavigation(
                    homeScreenViewModel = homeViewModel,
                    currentUserSessionDataViewModel = currentSessionDataViewModel,
                    newSearchViewModel = newSearchViewModel,
                    navigator = navigator,
                    drawerState = drawerState,
                    scope = scope
                )
            }
        }
        else -> {
            MainNavigation(
                homeScreenViewModel = homeViewModel,
                currentUserSessionDataViewModel = currentSessionDataViewModel,
                newSearchViewModel = newSearchViewModel,
                navigator = navigator,
                drawerState = drawerState,
                scope = scope
            )
        }
    }

    if (renameSearchPopUpState){
        RenamePopup(
            sessionDataViewModel = currentSessionDataViewModel
        )
    }

}
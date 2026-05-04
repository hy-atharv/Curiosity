package com.example.curiosity.core.navigation


import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.curiosity.core.models.CurrentUserSessionDataViewModel
import com.example.curiosity.presentation.Home.HomeScreen
import com.example.curiosity.presentation.Home.HomeScreenViewModel
import com.example.curiosity.presentation.NewSearch.NewSearchDrawer
import com.example.curiosity.presentation.NewSearch.NewSearchScreenViewModel
import com.example.curiosity.presentation.RecentSearch.RecentSearchDrawer
import kotlinx.coroutines.CoroutineScope

@Composable
fun MainNavigation(
    homeScreenViewModel: HomeScreenViewModel,
    currentUserSessionDataViewModel: CurrentUserSessionDataViewModel,
    newSearchViewModel: NewSearchScreenViewModel,
    navigator: NavHostController,
    drawerState: DrawerState,
    scope: CoroutineScope
){
    NavHost(navController = navigator, startDestination = HomeScreen){
        composable<HomeScreen> {
            HomeScreen(
                viewModel = homeScreenViewModel,
                currentUserSessionDataViewModel = currentUserSessionDataViewModel,
                drawerState = drawerState,
                scope = scope,
                navigator = navigator
            )
        }
        composable<NewSearchScreen> {
            val args = it.toRoute<NewSearchScreen>()
            NewSearchDrawer(
                viewModel = newSearchViewModel,
                currentUserSessionDataViewModel = currentUserSessionDataViewModel,
                drawerState = drawerState,
                scope = scope,
                navigator = navigator,
                searchId = args.searchId
            )
        }
        composable<RecentSearchScreen> {
            val args = it.toRoute<RecentSearchScreen>()
            RecentSearchDrawer(
                currentUserSessionDataViewModel = currentUserSessionDataViewModel,
                drawerState = drawerState,
                scope = scope,
                navigator = navigator,
                searchId = args.searchId
            )
        }
    }
}
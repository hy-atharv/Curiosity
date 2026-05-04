package com.example.curiosity.core.navigation

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.curiosity.core.models.CurrentUserSessionDataViewModel
import com.example.curiosity.presentation.DiscoveryChat.SearchDiscoveryChatScreen
import com.example.curiosity.presentation.Home.HomeScreenViewModel
import com.example.curiosity.presentation.Home.HomeSearchScreen
import com.example.curiosity.presentation.NewSearch.NewSearchScreen
import com.example.curiosity.presentation.NewSearch.NewSearchScreenViewModel
import com.example.curiosity.presentation.RecentSearch.RecentSearchScreen
import com.example.curiosity.presentation.RecentSearch.RecentSearchScreenViewModel
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun HomeSearchNavigation(
    viewModel: HomeScreenViewModel,
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope,
    drawerState: DrawerState,
    scope: CoroutineScope,
    mainNavigator: NavHostController,
    currentUserSessionDataViewModel: CurrentUserSessionDataViewModel
){
    val navigator = rememberNavController()

    NavHost(navController = navigator, startDestination = HomeSearchAndResultsScreen){
        composable<HomeSearchAndResultsScreen>{
            HomeSearchScreen(
                viewModel = viewModel,
                animatedVisibilityScope = animatedVisibilityScope,
                sharedTransitionScope = sharedTransitionScope,
                drawerState = drawerState,
                scope = scope,
                navigator = navigator,
                currentUserSessionDataViewModel = currentUserSessionDataViewModel
            )
        }
        composable<SearchAndDiscoveryChatScreen>(
            enterTransition = { slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(500)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(500)) }
        ){
            val args = it.toRoute<SearchAndDiscoveryChatScreen>()
            SearchDiscoveryChatScreen(
                navigator = navigator,
                searchId = args.searchId,
                searchQuestionTopic = args.questionTopic,
                searchQuestionQuery = args.questionQuery,
                currentUserSessionDataViewModel = currentUserSessionDataViewModel,
            )
        }
    }
}


@Composable
fun NewSearchNavigation(
    viewModel: NewSearchScreenViewModel,
    drawerState: DrawerState,
    scope: CoroutineScope,
    mainNavigator: NavHostController,
    currentUserSessionDataViewModel: CurrentUserSessionDataViewModel,
    searchId: Int
){
    val navigator = rememberNavController()

    NavHost(navController = navigator, startDestination = NewSearchAndResultsScreen){
        composable<NewSearchAndResultsScreen>{
            NewSearchScreen(
                viewModel = viewModel,
                drawerState = drawerState,
                scope = scope,
                navigator = navigator,
                currentUserSessionDataViewModel = currentUserSessionDataViewModel,
                searchId = searchId
            )
        }
        composable<SearchAndDiscoveryChatScreen>(
            enterTransition = { slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(500)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(500)) }
        ){
            val args = it.toRoute<SearchAndDiscoveryChatScreen>()
            SearchDiscoveryChatScreen(
                navigator = navigator,
                searchId = args.searchId,
                searchQuestionTopic = args.questionTopic,
                searchQuestionQuery = args.questionQuery,
                currentUserSessionDataViewModel = currentUserSessionDataViewModel,
            )
        }
    }
}


@Composable
fun RecentSearchNavigation(
    viewModel: RecentSearchScreenViewModel,
    drawerState: DrawerState,
    scope: CoroutineScope,
    mainNavigator: NavHostController,
    currentUserSessionDataViewModel: CurrentUserSessionDataViewModel,
    searchId: Int
){
    val navigator = rememberNavController()

    NavHost(navController = navigator, startDestination = RecentSearchAndResultsScreen){
        composable<RecentSearchAndResultsScreen>{
            RecentSearchScreen(
                viewModel = viewModel,
                drawerState = drawerState,
                scope = scope,
                navigator = navigator,
                currentUserSessionDataViewModel = currentUserSessionDataViewModel,
                searchId = searchId
            )
        }
        composable<SearchAndDiscoveryChatScreen>(
            enterTransition = { slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(500)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(500)) }
        ){
            val args = it.toRoute<SearchAndDiscoveryChatScreen>()
            SearchDiscoveryChatScreen(
                navigator = navigator,
                searchId = args.searchId,
                searchQuestionTopic = args.questionTopic,
                searchQuestionQuery = args.questionQuery,
                currentUserSessionDataViewModel = currentUserSessionDataViewModel,
            )
        }
    }
}
package com.tureq.rickmorty.ui


import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tureq.rickmorty.ui.detail.DetailScreen
import com.tureq.rickmorty.ui.detail.DetailViewModel
import com.tureq.rickmorty.ui.list.ListScreen
import com.tureq.rickmorty.ui.list.ListViewModel

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "list") {
        composable("list") {
            val vm: ListViewModel = hiltViewModel()
            ListScreen(
                state = vm.state,
                items = vm.items,
                favorites = vm.favorites,
                isAppending = vm.isAppending,
                onQueryChange = vm::onQueryChange,
                onRetry = vm::retry,
                onToggleFavorite = vm::toggleFavorite,
                onOpenDetail = { id -> navController.navigate("detail/$id") },
                onLoadNext = vm::loadNext
            )
        }
        composable(
            "detail/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) {
            val vm: DetailViewModel = hiltViewModel()
            DetailScreen(
                state = vm.state,
                isFavorite = vm.isFavorite,
                onToggleFavorite = vm::toggleFavorite
            )
        }
    }
}

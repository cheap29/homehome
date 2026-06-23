package com.homehome.app.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.homehome.app.data.repository.AppRepository
import com.homehome.app.ui.screen.*
import com.homehome.app.ui.viewmodel.*

object Routes {
    const val HOME = "home"
    const val TASK_BOX = "task_box"
    const val HABIT_WORDS = "habit_words"
    const val SELECT_THREE = "select_three"
    const val REFLECTION = "reflection"
    const val REFLECTION_COMPLETE = "reflection_complete/{sessionId}"
    const val HISTORY = "history"
    const val HISTORY_DETAIL = "history_detail/{sessionId}"

    fun reflectionComplete(sessionId: Long) = "reflection_complete/$sessionId"
    fun historyDetail(sessionId: Long) = "history_detail/$sessionId"
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    repository: AppRepository
) {
    NavHost(navController = navController, startDestination = Routes.HOME) {

        composable(Routes.HOME) {
            val vm: HomeViewModel = viewModel(factory = HomeViewModel.Factory(repository))
            HomeScreen(
                viewModel = vm,
                onNavigateToSelectThree = { navController.navigate(Routes.SELECT_THREE) },
                onNavigateToReflection = { navController.navigate(Routes.REFLECTION) },
                onNavigateToTaskBox = { navController.navigate(Routes.TASK_BOX) },
                onNavigateToHabitWords = { navController.navigate(Routes.HABIT_WORDS) },
                onNavigateToHistory = { navController.navigate(Routes.HISTORY) }
            )
        }

        composable(Routes.TASK_BOX) {
            val vm: TaskBoxViewModel = viewModel(factory = TaskBoxViewModel.Factory(repository))
            TaskBoxScreen(
                viewModel = vm,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.HABIT_WORDS) {
            val vm: HabitWordsViewModel = viewModel(factory = HabitWordsViewModel.Factory(repository))
            HabitWordsScreen(
                viewModel = vm,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.SELECT_THREE) {
            val vm: SelectThreeViewModel = viewModel(factory = SelectThreeViewModel.Factory(repository))
            SelectThreeScreen(
                viewModel = vm,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.REFLECTION) {
            val vm: ReflectionViewModel = viewModel(factory = ReflectionViewModel.Factory(repository))
            ReflectionScreen(
                viewModel = vm,
                onBack = { navController.popBackStack() },
                onComplete = { sessionId ->
                    navController.navigate(Routes.reflectionComplete(sessionId)) {
                        popUpTo(Routes.HOME)
                    }
                }
            )
        }

        composable(
            route = Routes.REFLECTION_COMPLETE,
            arguments = listOf(navArgument("sessionId") { type = NavType.LongType })
        ) { backStack ->
            val sessionId = backStack.arguments!!.getLong("sessionId")
            val vm: ReflectionCompleteViewModel = viewModel(
                factory = ReflectionCompleteViewModel.Factory(repository, sessionId)
            )
            ReflectionCompleteScreen(
                viewModel = vm,
                onNavigateToSelectThree = {
                    navController.navigate(Routes.SELECT_THREE) {
                        popUpTo(Routes.HOME)
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.HISTORY) {
            val vm: HistoryViewModel = viewModel(factory = HistoryViewModel.Factory(repository))
            HistoryScreen(
                viewModel = vm,
                onBack = { navController.popBackStack() },
                onDetail = { sessionId -> navController.navigate(Routes.historyDetail(sessionId)) }
            )
        }

        composable(
            route = Routes.HISTORY_DETAIL,
            arguments = listOf(navArgument("sessionId") { type = NavType.LongType })
        ) { backStack ->
            val sessionId = backStack.arguments!!.getLong("sessionId")
            val vm: HistoryDetailViewModel = viewModel(
                factory = HistoryDetailViewModel.Factory(repository, sessionId)
            )
            HistoryDetailScreen(
                viewModel = vm,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

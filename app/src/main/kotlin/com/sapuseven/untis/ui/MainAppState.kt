package com.sapuseven.untis.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope

@Composable
fun rememberMainAppState(
	coroutineScope: CoroutineScope = rememberCoroutineScope(),
	navController: NavHostController = rememberNavController(),
): MainAppState {
	return remember(
		navController,
		coroutineScope,
	) {
		MainAppState(
			navController = navController,
			coroutineScope = coroutineScope,
		)
	}
}

@Stable
class MainAppState(
	val navController: NavHostController,
	coroutineScope: CoroutineScope,
) {}

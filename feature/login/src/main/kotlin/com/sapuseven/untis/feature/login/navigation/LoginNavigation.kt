package com.sapuseven.untis.feature.login.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.sapuseven.untis.feature.login.LoginScreen
import com.sapuseven.untis.feature.login.datainput.LoginDataInputScreen
import kotlinx.serialization.Serializable

@Serializable
data object LoginRoute

@Serializable
data class LoginDataInputRoute(
	val userId: Long = -1,
	val schoolName: String? = null,
	val demoSchool: Boolean = false,
	val autoLogin: Boolean = false,
	val showProfileUpdate: Boolean = false,
)

fun NavController.navigateToLogin() {
	navigate(route = LoginRoute)
}

fun NavController.navigateToLoginDataInputExistingUser(userId: Long?) {
	navigate(route = LoginDataInputRoute(userId = userId ?: -1))
}

fun NavController.navigateToLoginDataInputExistingUserProfileUpdate(userId: Long) {
	navigate(route = LoginDataInputRoute(userId = userId, showProfileUpdate = true, autoLogin = true))
}

fun NavController.navigateToLoginDataInputFromSchoolSearch(schoolName: String) {
	navigate(route = LoginDataInputRoute(schoolName = schoolName))
}

fun NavController.navigateToLoginDataInputDemo(demoSchool: Boolean) {
	navigate(route = LoginDataInputRoute(demoSchool = demoSchool, autoLogin = true))
}

fun NavGraphBuilder.loginScreen(
	onBackClick: () -> Unit,
	onDemoClick: () -> Unit,
	onManualDataInputClick: () -> Unit,
) {
	composable<LoginRoute>(
		enterTransition = {
			fadeIn(animationSpec = tween(500))
		},
		exitTransition = {
			slideOutOfContainer(
				AnimatedContentTransitionScope.SlideDirection.Left,
				animationSpec = tween(500)
			)
		},
		popEnterTransition = {
			slideIntoContainer(
				AnimatedContentTransitionScope.SlideDirection.Right,
				animationSpec = tween(500)
			)
		},
		popExitTransition = {
			slideOutOfContainer(
				AnimatedContentTransitionScope.SlideDirection.Right,
				animationSpec = tween(500)
			)
		},
	) {
		LoginScreen(
			onDemoClick = onDemoClick,
			onManualDataInputClick = onManualDataInputClick,
		)
	}

	composable<LoginDataInputRoute>(
		enterTransition = {
			slideIntoContainer(
				AnimatedContentTransitionScope.SlideDirection.Left,
				animationSpec = tween(500)
			)
		},
		exitTransition = {
			slideOutOfContainer(
				AnimatedContentTransitionScope.SlideDirection.Down,
				animationSpec = tween(500)
			) + fadeOut(animationSpec = tween(500))
		},
		popEnterTransition = {
			slideIntoContainer(
				AnimatedContentTransitionScope.SlideDirection.Right,
				animationSpec = tween(500)
			)
		},
		popExitTransition = {
			slideOutOfContainer(
				AnimatedContentTransitionScope.SlideDirection.Right,
				animationSpec = tween(500)
			)
		},
		deepLinks = listOf(
			navDeepLink {
				uriPattern =
					"untis://setschool" // TODO how do I get the arbitrary query parameters into autoLoginData?
			}
		)
	) {
		LoginDataInputScreen(
			onBackClick = onBackClick
		)
	}
}

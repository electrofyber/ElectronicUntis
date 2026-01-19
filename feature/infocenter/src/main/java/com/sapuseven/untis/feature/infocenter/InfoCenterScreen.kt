package com.sapuseven.untis.feature.infocenter

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sapuseven.untis.core.ui.animation.fullscreenDialogAnimationEnter
import com.sapuseven.untis.core.ui.animation.fullscreenDialogAnimationExit
import com.sapuseven.untis.core.ui.common.NavigationBarInset
import com.sapuseven.untis.core.ui.functional.None
import com.sapuseven.untis.feature.infocenter.navigation.InfoCenterRoute
import com.sapuseven.untis.feature.infocenter.navigation.infoCenterPages
import com.sapuseven.untis.feature.infocenter.navigation.navigateToInfoCenterAbsences
import com.sapuseven.untis.feature.infocenter.navigation.navigateToInfoCenterEvents
import com.sapuseven.untis.feature.infocenter.navigation.navigateToInfoCenterMessages
import com.sapuseven.untis.feature.infocenter.navigation.navigateToInfoCenterOfficeHours

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoCenterScreen(
	viewModel: InfoCenterViewModel = hiltViewModel(),
	bottomNavController: NavHostController = rememberNavController(),
	onBackClick: () -> Unit,
) {
	var absenceFilterDialog by rememberSaveable { mutableStateOf(false) }

	val currentRoute by bottomNavController.currentBackStackEntryAsState()

	fun <T : Any> isCurrentRoute(route: T) =
		currentRoute?.destination?.route == route::class.qualifiedName

	Scaffold(
		topBar = {
			CenterAlignedTopAppBar(title = {
				Text(stringResource(id = R.string.feature_infocenter_title))
			}, navigationIcon = {
				IconButton(onClick = { onBackClick() }) {
					Icon(
						imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
						contentDescription = stringResource(id = com.sapuseven.untis.core.ui.R.string.all_back)
					)
				}
			}, actions = {
				if (isCurrentRoute(InfoCenterRoute.Absences)) {
					IconButton(
						onClick = { absenceFilterDialog = true }) {
						Icon(
							painter = painterResource(id = R.drawable.feature_infocenter_filter),
							contentDescription = null
						)
					}
				}
			})
		},
		bottomBar = {
			NavigationBarInset {
				NavBarItem(
					icon = R.drawable.feature_infocenter_messages,
					iconActive = R.drawable.feature_infocenter_messages_active,
					label = R.string.feature_infocenter_page_messagesofday,
					isActive = isCurrentRoute(InfoCenterRoute.Messages),
					onClick = bottomNavController::navigateToInfoCenterMessages
				)

				NavBarItem(
					icon = R.drawable.feature_infocenter_events,
					iconActive = R.drawable.feature_infocenter_events_active,
					label = R.string.feature_infocenter_page_events,
					isActive = isCurrentRoute(InfoCenterRoute.Events),
					onClick = bottomNavController::navigateToInfoCenterEvents
				)

				if (viewModel.shouldShowAbsences) {
					NavBarItem(
						icon = R.drawable.feature_infocenter_absences,
						iconActive = R.drawable.feature_infocenter_absences_active,
						label = R.string.feature_infocenter_page_absences,
						isActive = isCurrentRoute(InfoCenterRoute.Absences),
						onClick = bottomNavController::navigateToInfoCenterAbsences
					)
				}

				if (viewModel.shouldShowOfficeHours) {
					NavBarItem(
						icon = R.drawable.feature_infocenter_officehours,
						iconActive = R.drawable.feature_infocenter_officehours_active,
						label = R.string.feature_infocenter_page_officehours,
						isActive = isCurrentRoute(InfoCenterRoute.OfficeHours),
						onClick = bottomNavController::navigateToInfoCenterOfficeHours
					)
				}
			}
		},
		contentWindowInsets = WindowInsets.None
	) { innerPadding ->
		NavHost(
			navController = bottomNavController,
			startDestination = InfoCenterRoute.Messages,
			modifier = Modifier.padding(innerPadding)
		) {
			infoCenterPages()
		}

		/*Column(
			modifier = Modifier
				.padding(innerPadding)
				.fillMaxSize()
		) {
			Surface(
				modifier = Modifier
					.fillMaxWidth()
					.weight(1f)
			) {
				NavHost(
					navController = bottomNavController, startDestination = InfoCenterRoute.Messages
				) {
					infoCenterNav()
				}
			}
		}*/
	}

	AnimatedVisibility(
		visible = absenceFilterDialog,
		enter = fullscreenDialogAnimationEnter(),
		exit = fullscreenDialogAnimationExit()
	) {
		/*AbsenceFilterDialog(viewModel.userSettingsRepository) {
			absenceFilterDialog = false
		}*/
	}
}

@Composable
private fun RowScope.NavBarItem(
	@DrawableRes icon: Int,
	@DrawableRes iconActive: Int,
	@StringRes label: Int,
	isActive: Boolean,
	onClick: () -> Unit
) {
	NavigationBarItem(
		icon = {
			Icon(
				painterResource(
					id = if (isActive) iconActive else icon
				),
				contentDescription = null
			)
		},
		label = { Text(stringResource(label)) },
		selected = isActive,
		onClick = onClick
	)
}

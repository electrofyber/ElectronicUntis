package com.sapuseven.untis.ui.pages.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.sapuseven.untis.R
import com.sapuseven.untis.core.data.repository.UserState
import com.sapuseven.untis.core.datastore.GlobalSettingsDataSource
import com.sapuseven.untis.core.datastore.model.DarkTheme
import com.sapuseven.untis.core.datastore.model.UserSettings
import com.sapuseven.untis.core.ui.common.AppScaffold
import com.sapuseven.untis.core.ui.common.ReportsInfoBottomSheet
import com.sapuseven.untis.feature.login.navigation.LoginRoute
import com.sapuseven.untis.feature.timetable.navigation.TimetableRoute
import com.sapuseven.untis.helpers.AppTheme
import com.sapuseven.untis.helpers.ThemeMode
import com.sapuseven.untis.navigation.AppNavHost
import com.sapuseven.untis.ui.navigation.AppNavigator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContent(
	userState: UserState,
	globalSettings: GlobalSettingsDataSource,
	settingsFlow: Flow<UserSettings>,
	navigator: AppNavigator
) {
	val scope = rememberCoroutineScope()
	val settings by settingsFlow.collectAsState(initial = UserSettings.getDefaultInstance())

	val darkTheme = when (settings.darkTheme) {
		DarkTheme.DARK -> ThemeMode.AlwaysDark
		DarkTheme.LIGHT -> ThemeMode.AlwaysLight
		else -> ThemeMode.FollowSystem
	}
	val darkThemeOled by remember { derivedStateOf { settings.darkThemeOled } }
	val themeColor by remember { derivedStateOf { if (settings.hasThemeColor()) Color(settings.themeColor) else null } }

	AppTheme(darkTheme, darkThemeOled, themeColor) {
		Surface(modifier = Modifier.fillMaxSize()) {
			when (userState) {
				is UserState.Loading -> {
					AppScaffold(
						topBar = {
							CenterAlignedTopAppBar(
								title = { Text(stringResource(id = R.string.app_name)) },
								navigationIcon = {
									IconButton(onClick = {}) {
										Icon(
											imageVector = Icons.Outlined.Menu,
											contentDescription = null
										)
									}
								},
								colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
									containerColor = Color.Transparent,
									scrolledContainerColor = Color.Transparent
								)
							)
						},
						modifier = Modifier.safeDrawingPadding()
					) {}
				}

				is UserState.NoUsers -> {
					AppNavHost(
						navigator = navigator,
						startDestination = LoginRoute
					)
				}

				is UserState.User -> {
					key(userState.user.id) {
						AppNavHost(
							navigator = navigator,
							startDestination = TimetableRoute()
						)
					}

					val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
					LaunchedEffect(globalSettings) {
						globalSettings.getSettings().first().let {
							if (!it.errorReportingSet) {
								bottomSheetState.show()
							}
						}
					}
					ReportsInfoBottomSheet(bottomSheetState) {
						scope.launch {
							globalSettings.updateSettings {
								errorReportingSet = true
							}
							bottomSheetState.hide()
						}
					}
				}
			}
		}
	}
}

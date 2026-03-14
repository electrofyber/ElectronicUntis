package com.sapuseven.untis.feature.settings.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sapuseven.untis.core.ui.functional.bottomInsets
import com.electrofyber.untis.feature.settings.R
import com.sapuseven.untis.feature.settings.SettingsCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
	onBackPressed: () -> Unit,
	onNavigate: (route: Any) -> Unit,
) {
	Scaffold(
		topBar = {
			CenterAlignedTopAppBar(
				title = {
					Text(stringResource(id = R.string.feature_settings_activity_title_settings))
				},
				navigationIcon = {
					IconButton(onClick = onBackPressed) {
						Icon(
							imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
							contentDescription = stringResource(id = com.sapuseven.untis.core.ui.R.string.all_back)
						)
					}
				}
			)
		}
	) { innerPadding ->
		Column(
			modifier = Modifier
				.verticalScroll(rememberScrollState())
				.padding(innerPadding)
				.bottomInsets()
				.fillMaxSize()
		) {
			Spacer(modifier = Modifier.height(16.dp))

			SettingsCategory(
				key = SettingsRoute.General,
				title = { Text(stringResource(id = R.string.feature_settings_preferences_general)) },
				summary = { Text(stringResource(id = R.string.feature_settings_preferences_general_summary)) },
				icon = {
					Icon(
						painter = painterResource(id = R.drawable.feature_settings_general),
						contentDescription = null
					)
				},
				onNavigate = onNavigate,
				isFirst = true
			)

			SettingsCategory(
				key = SettingsRoute.Styling,
				title = { Text(stringResource(id = R.string.feature_settings_preferences_styling)) },
				summary = { Text(stringResource(id = R.string.feature_settings_preferences_styling_summary)) },
				icon = {
					Icon(
						painter = painterResource(id = R.drawable.settings_styling),
						contentDescription = null
					)
				},
				onNavigate = onNavigate
			)

			SettingsCategory(
				key = SettingsRoute.Timetable(),
				title = { Text(stringResource(id = R.string.feature_settings_preferences_timetable)) },
				summary = { Text(stringResource(id = R.string.feature_settings_preferences_timetable_summary)) },
				icon = {
					Icon(
						painter = painterResource(id = R.drawable.settings_timetable),
						contentDescription = null
					)
				},
				onNavigate = onNavigate
			)

			SettingsCategory(
				key = SettingsRoute.Notifications,
				title = { Text(stringResource(id = R.string.feature_settings_preferences_notifications)) },
				summary = { Text(stringResource(id = R.string.feature_settings_preferences_notifications_summary)) },
				icon = {
					Icon(
						painter = painterResource(id = R.drawable.settings_notifications),
						contentDescription = null
					)
				},
				onNavigate = onNavigate
			)

			SettingsCategory(
				key = SettingsRoute.About,
				title = { Text(stringResource(id = R.string.feature_settings_preferences_info)) },
				summary = { Text(stringResource(id = R.string.feature_settings_preferences_info_summary)) },
				icon = {
					Icon(
						painter = painterResource(id = R.drawable.feature_settings_info),
						contentDescription = null
					)
				},
				onNavigate = onNavigate,
				isLast = true
			)
		}
	}
}

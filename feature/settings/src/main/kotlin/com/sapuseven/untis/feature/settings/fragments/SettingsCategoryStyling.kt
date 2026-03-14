package com.sapuseven.untis.feature.settings.fragments

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import com.electrofyber.untis.feature.settings.R
import com.sapuseven.compose.protostore.ui.preferences.ColorPreference
import com.sapuseven.compose.protostore.ui.preferences.ListPreference
import com.sapuseven.compose.protostore.ui.preferences.PreferenceGroup
import com.sapuseven.compose.protostore.ui.preferences.SwitchPreference
import com.sapuseven.untis.core.datastore.model.DarkTheme
import com.sapuseven.untis.feature.settings.SettingsViewModel
import com.sapuseven.untis.feature.settings.util.withDefault

@Composable
fun SettingsCategoryStyling(viewModel: SettingsViewModel) {
	val defaultThemeColor = MaterialTheme.colorScheme.primary

	PreferenceGroup(stringResource(id = R.string.feature_settings_preference_category_styling_colors)) {
		ColorPreference(
			title = { Text(stringResource(R.string.feature_settings_preference_marker)) },
			settingsDataSource = viewModel.userSettingsDataSource,
			value = { it.marker.withDefault(it.hasMarker(), it.defaultInstanceForType.marker) },
			onValueChange = { marker = it ?: run { clearMarker(); return@ColorPreference }},
			defaultColorLabel = stringResource(R.string.feature_settings_preferences_default_color)
		)
	}

	PreferenceGroup(stringResource(id = R.string.feature_settings_preference_category_styling_themes)) {
		ColorPreference(
			title = { Text(stringResource(R.string.feature_settings_preferences_theme_color)) },
			leadingContent = {
				Icon(
					painter = painterResource(R.drawable.settings_timetable_format_paint),
					contentDescription = null
				)
			},
			settingsDataSource = viewModel.userSettingsDataSource,
			value = { it.themeColor.withDefault(it.hasThemeColor(), defaultThemeColor.toArgb()) },
			onValueChange = {
				themeColor = it ?: run { clearThemeColor(); return@ColorPreference }
			},
			defaultColor = defaultThemeColor,
			defaultColorLabel = stringResource(id = R.string.feature_settings_preferences_theme_color_system)
		)

		ListPreference(
			title = { Text(stringResource(R.string.feature_settings_preference_dark_theme)) },
			supportingContent = { value, _ -> Text(value.second) },
			leadingContent = {
				Icon(
					painter = painterResource(R.drawable.settings_timetable_brightness_medium),
					contentDescription = null
				)
			},
			entries = stringArrayResource(id = R.array.feature_settings_preference_dark_theme_values),
			entryLabels = stringArrayResource(id = R.array.feature_settings_preference_dark_theme),
			settingsDataSource = viewModel.userSettingsDataSource,
			value = { it.darkTheme.number.toString() },
			onValueChange = { darkTheme = DarkTheme.forNumber(it.toInt()) }
		)

		SwitchPreference(
			title = { Text(stringResource(R.string.feature_settings_preference_dark_theme_oled)) },
			summary = { Text(stringResource(R.string.feature_settings_preference_dark_theme_oled_desc)) },
			leadingContent = {
				Icon(
					painter = painterResource(R.drawable.settings_timetable_format_oled),
					contentDescription = null
				)
			},
			enabledCondition = { it.darkTheme != DarkTheme.LIGHT },
			settingsDataSource = viewModel.userSettingsDataSource,
			value = { it.darkThemeOled },
			onValueChange = { darkThemeOled = it }
		)
	}
}

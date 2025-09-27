package com.sapuseven.untis.feature.settings.fragments

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import com.sapuseven.compose.protostore.ui.preferences.ColorPreference
import com.sapuseven.compose.protostore.ui.preferences.ConfirmDialogPreference
import com.sapuseven.compose.protostore.ui.preferences.ListPreference
import com.sapuseven.compose.protostore.ui.preferences.MultiSelectListPreference
import com.sapuseven.compose.protostore.ui.preferences.PreferenceGroup
import com.sapuseven.compose.protostore.ui.preferences.SwitchPreference
import com.sapuseven.untis.core.datastore.model.DarkTheme
import com.sapuseven.untis.feature.settings.R
import com.sapuseven.untis.feature.settings.SettingsViewModel
import com.sapuseven.untis.feature.settings.util.withDefault

@Composable
fun SettingsCategoryStyling(viewModel: SettingsViewModel) {
	// *past colors are currently disabled as an "experiment" to simplify color preferences

	val defaultThemeColor = MaterialTheme.colorScheme.primary
	val defaultBackgroundRegular = MaterialTheme.colorScheme.primary // TODO EventStyle.ThemePrimary.color()
	//val defaultBackgroundRegularPast = EventColor.ThemePrimary.pastColor()
	val defaultBackgroundExam = MaterialTheme.colorScheme.error // TODO EventStyle.ThemeError.color()
	//val defaultBackgroundExamPast = EventColor.ThemeError.pastColor()
	val defaultBackgroundIrregular = MaterialTheme.colorScheme.tertiary // TODO EventStyle.ThemeTertiary.color()
	//val defaultBackgroundIrregularPast = EventColor.ThemeTertiary.pastColor()
	val defaultBackgroundCancelled = MaterialTheme.colorScheme.secondary // TODO EventStyle.ThemeSecondary.color()
	//val defaultBackgroundCancelledPast = EventColor.ThemeSecondary.pastColor()

	PreferenceGroup(stringResource(id = R.string.feature_settings_preference_category_styling_colors)) {
		ColorPreference(
			title = { Text(stringResource(R.string.feature_settings_preference_background_future)) },
			showAlphaSlider = true,
			settingsDataSource = viewModel.userSettingsDataSource,
			value = { it.backgroundFuture.withDefault(it.hasBackgroundFuture(), it.defaultInstanceForType.backgroundFuture) },
			onValueChange = { backgroundFuture = it ?: run { clearBackgroundFuture(); return@ColorPreference }},
			defaultColorLabel = stringResource(R.string.feature_settings_preferences_default_color)
		)

		ColorPreference(
			title = { Text(stringResource(R.string.feature_settings_preference_background_past)) },
			showAlphaSlider = true,
			settingsDataSource = viewModel.userSettingsDataSource,
			value = { it.backgroundPast.withDefault(it.hasBackgroundPast(), it.defaultInstanceForType.backgroundPast) },
			onValueChange = { backgroundPast = it ?: run { clearBackgroundPast(); return@ColorPreference }},
			defaultColorLabel = stringResource(R.string.feature_settings_preferences_default_color)
		)

		ColorPreference(
			title = { Text(stringResource(R.string.feature_settings_preference_marker)) },
			settingsDataSource = viewModel.userSettingsDataSource,
			value = { it.marker.withDefault(it.hasMarker(), it.defaultInstanceForType.marker) },
			onValueChange = { marker = it ?: run { clearMarker(); return@ColorPreference }},
			defaultColorLabel = stringResource(R.string.feature_settings_preferences_default_color)
		)
	}

	PreferenceGroup(stringResource(id = R.string.feature_settings_preference_category_styling_backgrounds)) {
		MultiSelectListPreference(
			title = { Text(stringResource(R.string.feature_settings_preference_school_background)) },
			summary = { Text(stringResource(R.string.feature_settings_preference_school_background_desc)) },
			entries = stringArrayResource(id = R.array.feature_settings_preference_schoolcolors_values),
			entryLabels = stringArrayResource(id = R.array.feature_settings_preference_schoolcolors),
			settingsDataSource = viewModel.userSettingsDataSource,
			value = { it.schoolBackgroundList.toSet() },
			onValueChange = {
				clearSchoolBackground()
				addAllSchoolBackground(it)
			}
		)

		ColorPreference(
			title = { Text(stringResource(R.string.feature_settings_preference_background_regular)) },
			enabledCondition = {
				!it.schoolBackgroundList.contains("regular")
			},
			settingsDataSource = viewModel.userSettingsDataSource,
			value = { it.backgroundRegular.withDefault(it.hasBackgroundRegular(), defaultBackgroundRegular.toArgb()) },
			onValueChange = { backgroundRegular = it ?: run { clearBackgroundRegular(); return@ColorPreference }  },
			showAlphaSlider = true,
			defaultColor = defaultBackgroundRegular,
			defaultColorLabel = stringResource(id = R.string.feature_settings_preferences_theme_color)
		)

		/*ColorPreference(
			title = { Text(stringResource(R.string.feature_settings_preference_background_regular_past)) },
			enabledCondition = {
				!it.schoolBackgroundList.contains("regular")
			},
			settingsDataSource = viewModel.userSettingsDataSource,
			value = { it.backgroundRegularPast.withDefault(it.hasBackgroundRegularPast(), defaultBackgroundRegularPast.toArgb()) },
			onValueChange = { backgroundRegularPast = it ?: run { clearBackgroundRegularPast(); return@ColorPreference }  },
			showAlphaSlider = true,
			defaultColor = defaultBackgroundRegularPast,
			defaultColorLabel = stringResource(id = R.string.feature_settings_preferences_theme_color)
		)*/

		ColorPreference(
			title = { Text(stringResource(R.string.feature_settings_preference_background_exam)) },
			enabledCondition = {
				!it.schoolBackgroundList.contains("exam")
			},
			settingsDataSource = viewModel.userSettingsDataSource,
			value = { it.backgroundExam.withDefault(it.hasBackgroundExam(), defaultBackgroundExam.toArgb()) },
			onValueChange = { backgroundExam = it ?: run { clearBackgroundExam(); return@ColorPreference }  },
			showAlphaSlider = true,
			defaultColor = defaultBackgroundExam,
			defaultColorLabel = stringResource(id = R.string.feature_settings_preferences_theme_color)
		)

		/*ColorPreference(
			title = { Text(stringResource(R.string.feature_settings_preference_background_exam_past)) },
			enabledCondition = {
				!it.schoolBackgroundList.contains("exam")
			},
			settingsDataSource = viewModel.userSettingsDataSource,
			value = { it.backgroundExamPast.withDefault(it.hasBackgroundExamPast(), defaultBackgroundExamPast.toArgb()) },
			onValueChange = { backgroundExamPast = it ?: run { clearBackgroundExamPast(); return@ColorPreference }  },
			showAlphaSlider = true,
			defaultColor = defaultBackgroundExamPast,
			defaultColorLabel = stringResource(id = R.string.feature_settings_preferences_theme_color)
		)*/

		ColorPreference(
			title = { Text(stringResource(R.string.feature_settings_preference_background_irregular)) },
			enabledCondition = {
				!it.schoolBackgroundList.contains("irregular")
			},
			settingsDataSource = viewModel.userSettingsDataSource,
			value = { it.backgroundIrregular.withDefault(it.hasBackgroundIrregular(), defaultBackgroundIrregular.toArgb()) },
			onValueChange = { backgroundIrregular = it ?: run { clearBackgroundIrregular(); return@ColorPreference }  },
			showAlphaSlider = true,
			defaultColor = defaultBackgroundIrregular,
			defaultColorLabel = stringResource(id = R.string.feature_settings_preferences_theme_color)
		)

		/*ColorPreference(
			title = { Text(stringResource(R.string.feature_settings_preference_background_irregular_past)) },
			enabledCondition = {
				!it.schoolBackgroundList.contains("irregular")
			},
			settingsDataSource = viewModel.userSettingsDataSource,
			value = { it.backgroundIrregularPast.withDefault(it.hasBackgroundIrregularPast(), defaultBackgroundIrregularPast.toArgb()) },
			onValueChange = { backgroundIrregularPast = it ?: run { clearBackgroundIrregularPast(); return@ColorPreference }  },
			showAlphaSlider = true,
			defaultColor = defaultBackgroundIrregularPast,
			defaultColorLabel = stringResource(id = R.string.feature_settings_preferences_theme_color)
		)*/

		ColorPreference(
			title = { Text(stringResource(R.string.feature_settings_preference_background_cancelled)) },
			enabledCondition = {
				!it.schoolBackgroundList.contains("cancelled")
			},
			settingsDataSource = viewModel.userSettingsDataSource,
			value = { it.backgroundCancelled.withDefault(it.hasBackgroundCancelled(), defaultBackgroundCancelled.toArgb()) },
			onValueChange = { backgroundCancelled = it ?: run { clearBackgroundCancelled(); return@ColorPreference }  },
			showAlphaSlider = true,
			defaultColor = defaultBackgroundCancelled,
			defaultColorLabel = stringResource(id = R.string.feature_settings_preferences_theme_color)
		)

		/*ColorPreference(
			title = { Text(stringResource(R.string.feature_settings_preference_background_cancelled_past)) },
			enabledCondition = {
				!it.schoolBackgroundList.contains("cancelled")
			},
			settingsDataSource = viewModel.userSettingsDataSource,
			value = { it.backgroundCancelledPast.withDefault(it.hasBackgroundCancelledPast(), defaultBackgroundCancelledPast.toArgb()) },
			onValueChange = { backgroundCancelledPast = it ?: run { clearBackgroundCancelledPast(); return@ColorPreference }  },
			showAlphaSlider = true,
			defaultColor = defaultBackgroundCancelledPast,
			defaultColorLabel = stringResource(id = R.string.feature_settings_preferences_theme_color)
		)*/

		ConfirmDialogPreference(
			title = { Text(stringResource(R.string.feature_settings_preference_timetable_colors_reset)) },
			summary = { Text(stringResource(R.string.feature_settings_preference_timetable_colors_reset_desc)) },
			leadingContent = {
				Icon(
					painter = painterResource(R.drawable.settings_reset),
					contentDescription = null
				)
			},
			dialogTitle = { Text(stringResource(R.string.feature_settings_preference_dialog_colors_reset_title)) },
			dialogText = { Text(stringResource(R.string.feature_settings_preference_dialog_colors_reset_text)) },
			onConfirm = {
				viewModel.resetColors()
			}
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

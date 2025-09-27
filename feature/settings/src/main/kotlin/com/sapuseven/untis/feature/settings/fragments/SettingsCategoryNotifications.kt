package com.sapuseven.untis.feature.settings.fragments

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.sapuseven.compose.protostore.ui.preferences.ListPreference
import com.sapuseven.compose.protostore.ui.preferences.NumericInputPreference
import com.sapuseven.compose.protostore.ui.preferences.Preference
import com.sapuseven.compose.protostore.ui.preferences.PreferenceGroup
import com.sapuseven.compose.protostore.ui.preferences.SwitchPreference
import com.sapuseven.untis.core.datastore.model.NotificationVisibility
import com.sapuseven.untis.core.ui.common.disabled
import com.sapuseven.untis.feature.settings.R
import com.sapuseven.untis.feature.settings.SettingsViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SettingsCategoryNotifications(viewModel: SettingsViewModel) {
	val scope = rememberCoroutineScope()
	val context = LocalContext.current

	var notificationsMessageVisible by rememberSaveable { mutableStateOf(false) }

	val notificationPermissionsState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
		rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS) {
			notificationsMessageVisible = !it

			scope.launch {
				viewModel.userSettingsDataSource.updateSettings {
					notificationsEnable = it
				}
			}
		}
	else null

	fun enqueueNotificationSetup() = scope.launch {
		delay(1000) // Non-time-critical; Delay a bit to ensure the settings are updated before worker is enqueued
		/*WorkManager.getInstance(context).apply {
			enqueue(OneTimeWorkRequestBuilder<NotificationSetupWorker>().build())
		}*/
	}

	fun clearNotifications() {
		(context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager)?.cancelAll()
	}

	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
		val visible by viewModel.userSettingsDataSource.getSettings().map { it.notificationsEnable }
			.collectAsState(initial = false)
		ScheduleExactAlarmInfoMessage(
			visible = visible,
			primaryText = R.string.feature_settings_preference_notifications_exact_alarms_unavailable,
			secondaryText = stringResource(
				R.string.feature_settings_preference_notifications_exact_alarms_unavailable_desc,
				stringResource(com.sapuseven.untis.core.ui.R.string.app_name)
			)
		)
	}

	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
		val notificationsEnabled by viewModel.userSettingsDataSource.getSettings().map { it.notificationsEnable }
			.collectAsState(initial = false)
		NotificationsInfoMessage(visible = !notificationsEnabled && notificationsMessageVisible)
	}

	PreferenceGroup(
		title = stringResource(R.string.feature_settings_preference_category_notifications_break)
	) {
		LaunchedEffect(Unit) {
			notificationPermissionsState?.let {
				if (!it.status.isGranted) {
					viewModel.userSettingsDataSource.updateSettings {
						notificationsEnable = false
					}
				}
			}
		}

		SwitchPreference(
			title = { Text(stringResource(R.string.feature_settings_preference_notifications_enable)) },
			summary = { Text(stringResource(R.string.feature_settings_preference_notifications_enable_desc)) },
			settingsDataSource = viewModel.userSettingsDataSource,
			value = { it.notificationsEnable },
			onValueChange = {
				notificationsEnable = if (it) {
					if (notificationPermissionsState?.status?.isGranted != false) {
						notificationsMessageVisible = false
						enqueueNotificationSetup()
						true
					} else {
						notificationPermissionsState.launchPermissionRequest()
						false
					}
				} else {
					clearNotifications()
					false
				}
			}
		)

		SwitchPreference(
			title = { Text(stringResource(R.string.feature_settings_preference_notifications_multiple)) },
			summary = { Text(stringResource(R.string.feature_settings_preference_notifications_multiple_desc)) },
			enabledCondition = { it.notificationsEnable },
			settingsDataSource = viewModel.userSettingsDataSource,
			value = { it.notificationsInMultiple },
			onValueChange = {
				notificationsInMultiple = it
				enqueueNotificationSetup()
			}
		)

		SwitchPreference(
			title = { Text(stringResource(R.string.feature_settings_preference_notifications_first_lesson)) },
			summary = { Text(stringResource(R.string.feature_settings_preference_notifications_first_lesson_desc)) },
			enabledCondition = { it.notificationsEnable },
			settingsDataSource = viewModel.userSettingsDataSource,
			value = { it.notificationsBeforeFirst },
			onValueChange = {
				notificationsBeforeFirst = it
				enqueueNotificationSetup()
			}
		)

		NumericInputPreference(
			title = { Text(stringResource(R.string.feature_settings_preference_notifications_first_lesson_time)) },
			unit = stringResource(R.string.feature_settings_preference_notifications_first_lesson_time_unit),
			enabledCondition = { it.notificationsEnable && it.notificationsBeforeFirst },
			settingsDataSource = viewModel.userSettingsDataSource,
			value = { it.notificationsBeforeFirstTime },
			onValueChange = {
				notificationsBeforeFirstTime = it
				enqueueNotificationSetup()
			}
		)
	}

	PreferenceGroup(stringResource(id = R.string.feature_settings_preference_category_notifications_visible_fields)) {
		ListPreference(
			title = { Text(stringResource(com.sapuseven.untis.core.ui.R.string.all_subjects)) },
			supportingContent = { value, enabled ->
				Text(
					value.second,
					modifier = Modifier.disabled(!enabled)
				)
			},
			leadingContent = {
				Icon(
					painter = painterResource(com.sapuseven.untis.core.ui.R.drawable.core_ui_subject),
					contentDescription = null
				)
			},
			entries = stringArrayResource(id = R.array.feature_settings_preference_notifications_visibility_values),
			entryLabels = stringArrayResource(id = R.array.feature_settings_preference_notifications_visibility),
			enabledCondition = { it.notificationsEnable },
			settingsDataSource = viewModel.userSettingsDataSource,
			value = { it.notificationsVisibilitySubjects.number.toString() },
			onValueChange = { notificationsVisibilitySubjects = NotificationVisibility.forNumber(it.toInt()) }
		)

		ListPreference(
			title = { Text(stringResource(com.sapuseven.untis.core.ui.R.string.all_rooms)) },
			supportingContent = { value, enabled ->
				Text(
					value.second,
					modifier = Modifier.disabled(!enabled)
				)
			},
			leadingContent = {
				Icon(
					painter = painterResource(com.sapuseven.untis.core.ui.R.drawable.core_ui_rooms),
					contentDescription = null
				)
			},
			entries = stringArrayResource(id = R.array.feature_settings_preference_notifications_visibility_values),
			entryLabels = stringArrayResource(id = R.array.feature_settings_preference_notifications_visibility),
			enabledCondition = { it.notificationsEnable },
			settingsDataSource = viewModel.userSettingsDataSource,
			value = { it.notificationsVisibilityRooms.number.toString() },
			onValueChange = { notificationsVisibilityRooms = NotificationVisibility.forNumber(it.toInt()) }
		)

		ListPreference(
			title = { Text(stringResource(com.sapuseven.untis.core.ui.R.string.all_teachers)) },
			supportingContent = { value, enabled ->
				Text(
					value.second,
					modifier = Modifier.disabled(!enabled)
				)
			},
			leadingContent = {
				Icon(
					painter = painterResource(com.sapuseven.untis.core.ui.R.drawable.core_ui_teachers),
					contentDescription = null
				)
			},
			entries = stringArrayResource(id = R.array.feature_settings_preference_notifications_visibility_values),
			entryLabels = stringArrayResource(id = R.array.feature_settings_preference_notifications_visibility),
			enabledCondition = { it.notificationsEnable },
			settingsDataSource = viewModel.userSettingsDataSource,
			value = { it.notificationsVisibilityTeachers.number.toString() },
			onValueChange = { notificationsVisibilityTeachers = NotificationVisibility.forNumber(it.toInt()) }
		)

		ListPreference(
			title = { Text(stringResource(com.sapuseven.untis.core.ui.R.string.all_classes)) },
			supportingContent = { value, enabled ->
				Text(
					value.second,
					modifier = Modifier.disabled(!enabled)
				)
			},
			leadingContent = {
				Icon(
					painter = painterResource(com.sapuseven.untis.core.ui.R.drawable.core_ui_classes),
					contentDescription = null
				)
			},
			entries = stringArrayResource(id = R.array.feature_settings_preference_notifications_visibility_values),
			entryLabels = stringArrayResource(id = R.array.feature_settings_preference_notifications_visibility),
			enabledCondition = { it.notificationsEnable },
			settingsDataSource = viewModel.userSettingsDataSource,
			value = { it.notificationsVisibilityClasses.number.toString() },
			onValueChange = { notificationsVisibilityClasses = NotificationVisibility.forNumber(it.toInt()) }
		)
	}
	Preference(
		title = { Text(stringResource(R.string.feature_settings_preference_notifications_clear)) },
		onClick = {
			clearNotifications()
		},
		leadingContent = {
			Icon(
				painter = painterResource(R.drawable.settings_notifications_clear_all),
				contentDescription = null
			)
		}
	)
}

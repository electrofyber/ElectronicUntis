package com.sapuseven.untis.feature.notifications

import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Build
import com.sapuseven.untis.core.datastore.UserSettingsDataSource
import com.sapuseven.untis.core.domain.worker.TimetableHandler
import com.sapuseven.untis.core.model.timetable.Timetable
import com.sapuseven.untis.core.model.user.User
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationScheduler @Inject constructor(
	@field:ApplicationContext private val context: Context,
	private val userSettingsDataSource: UserSettingsDataSource,
) : TimetableHandler {

	override suspend fun isEnabled(user: User): Boolean {
		return userSettingsDataSource.getSettings(user.id).first().notificationsEnable && canPostNotifications()
	}

	override suspend fun onNewTimetable(user: User, timetable: Timetable) {
		if (!isEnabled(user)) return

		// TODO: rescheduleUseCase(user, timetable)
	}

	private fun canPostNotifications(): Boolean {
		val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
		return (Build.VERSION.SDK_INT < Build.VERSION_CODES.N || notificationManager.areNotificationsEnabled())
	}
}

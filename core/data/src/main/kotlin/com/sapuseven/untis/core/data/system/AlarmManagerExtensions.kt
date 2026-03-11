package com.sapuseven.untis.core.data.system

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.os.Build
import androidx.annotation.RequiresPermission
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

@RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
fun AlarmManager.setBest(time: LocalDateTime, pendingIntent: PendingIntent) {
	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && canScheduleExactAlarms()) {
		setExact(
			AlarmManager.RTC_WAKEUP,
			time.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds(),
			pendingIntent
		)
	} else {
		setWindow(
			AlarmManager.RTC_WAKEUP,
			time.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds(),
			600_000, // 10 minutes
			pendingIntent
		)
	}
}

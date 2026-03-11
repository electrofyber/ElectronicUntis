package com.sapuseven.untis.core.domain.worker

import com.sapuseven.untis.core.model.timetable.Timetable
import com.sapuseven.untis.core.model.user.User

interface TimetableHandler {
	suspend fun isEnabled(user: User): Boolean
	suspend fun onNewTimetable(user: User, timetable: Timetable)
}

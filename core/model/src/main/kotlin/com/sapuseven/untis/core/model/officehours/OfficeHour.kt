package com.sapuseven.untis.core.model.officehours

import com.sapuseven.untis.core.model.timetable.Element
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class OfficeHour(
	val id: Long,
	val teacher: Element,
	val startDateTime: LocalDateTime,
	val endDateTime: LocalDateTime,
	//val imageId: Long,
	val displayNameRooms: String,
	val registrationInfo: OfficeHourRegistrationInfo
)

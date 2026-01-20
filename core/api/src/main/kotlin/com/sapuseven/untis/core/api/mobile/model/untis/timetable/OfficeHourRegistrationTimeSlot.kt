package com.sapuseven.untis.core.api.mobile.model.untis.timetable

import com.sapuseven.untis.core.api.mobile.model.untis.enumeration.OfficeHourRegistrationTimeSlotState
import com.sapuseven.untis.core.api.serializer.Time
import kotlinx.serialization.Serializable


@Serializable
data class OfficeHourRegistrationTimeSlot(
	val startTime: Time,
	val endTime: Time,
	val state: OfficeHourRegistrationTimeSlotState
)

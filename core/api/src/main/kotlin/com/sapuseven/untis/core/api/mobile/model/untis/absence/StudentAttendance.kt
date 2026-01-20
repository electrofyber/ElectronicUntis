package com.sapuseven.untis.core.api.mobile.model.untis.absence

import kotlinx.serialization.Serializable

@Serializable
data class StudentAttendance(
	val studentId: Long,
	val startDateTime: String,
	val endDateTime: String
)

package com.sapuseven.untis.core.api.model.untis.timetable

import kotlinx.serialization.Serializable

@Serializable
data class PeriodExam(
	val id: Long,
	val examtype: String?,
	val name: String?,
	val text: String?
)

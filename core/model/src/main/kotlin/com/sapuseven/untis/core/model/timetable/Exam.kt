package com.sapuseven.untis.core.model.timetable

import kotlinx.serialization.Serializable

@Serializable
data class Exam(
	val id: Long,
	val type: String?,
	val name: String?,
	val text: String?
)

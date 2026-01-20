package com.sapuseven.untis.core.api.mobile.model.untis.timetable

import kotlinx.serialization.Serializable


@Serializable
data class Lesson(
	val id: Long,
	val subjectId: Long,
	val klassenIds: List<Long> = emptyList(),
	val teacherIds: List<Long> = emptyList()
)

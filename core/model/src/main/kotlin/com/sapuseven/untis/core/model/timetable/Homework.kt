package com.sapuseven.untis.core.model.timetable

import kotlinx.datetime.LocalDate

data class Homework(
	val id: Long,
	val lessonId: Long,
	val startDate: LocalDate,
	val endDate: LocalDate,
	val text: String,
	val attachments: List<Attachment>,
	val completed: Boolean
)

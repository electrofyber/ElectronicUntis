package com.sapuseven.untis.core.model.absences

import kotlinx.datetime.LocalDateTime

data class Absence(
	val id: Long,
	val studentId: Long,
	val klasseId: Long,
	val startDateTime: LocalDateTime,
	val endDateTime: LocalDateTime,
	val owner: Boolean,
	val excused: Boolean,
	val excuse: Excuse?,
	val absenceReason: String,
	val text: String,
)

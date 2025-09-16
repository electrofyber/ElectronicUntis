package com.sapuseven.untis.core.data.mapper

import com.sapuseven.untis.core.model.timetable.Exam

internal fun com.sapuseven.untis.core.api.model.untis.timetable.PeriodExam.toDomain() = Exam(
	id = id,
	type = examtype,
	name = name,
	text = text,
)

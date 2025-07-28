package com.sapuseven.untis.core.data.mapper

import com.sapuseven.untis.core.model.Homework

internal fun com.sapuseven.untis.core.api.model.untis.classreg.HomeWork.toDomain() = Homework(
	id = id,
	lessonId = lessonId,
	startDate = startDate,
	endDate = endDate,
	text = text,
	attachments = attachments.map { it.toDomain() },
	completed = completed,
)

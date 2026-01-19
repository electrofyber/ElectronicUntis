package com.sapuseven.untis.core.data.mapper

import com.sapuseven.untis.core.api.model.untis.classreg.HomeWork
import com.sapuseven.untis.core.model.timetable.Element
import com.sapuseven.untis.core.model.timetable.ElementKey
import com.sapuseven.untis.core.model.timetable.Homework as DomainHomework

internal fun HomeWork.toDomain(
	allElements: Map<ElementKey, Element>
) = DomainHomework(
	id = id,
	text = text,
	startDate = startDate,
	endDate = endDate,
	attachments = attachments.map { it.toDomain() },
	completed = completed,
)

/* internal fun Exam.toDomain(
	allElements: Map<ElementKey, Element>
) = DomainExam(
	id = id,
	type = examType,
	startDateTime = startDateTime,
	endDateTime = endDateTime,
	subject = allElements[ElementKey(subjectId, ElementType.SUBJECT)],
	classes = klasseIds.mapNotNull { allElements[ElementKey(it, ElementType.CLASS)] },
	rooms = roomIds.mapNotNull { allElements[ElementKey(it, ElementType.ROOM)] },
	teachers = teacherIds.mapNotNull { allElements[ElementKey(it, ElementType.TEACHER)] },
	//invigilators = invigilators,
	name = name,
	text = text,
)*/

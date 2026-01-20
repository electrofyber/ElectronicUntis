package com.sapuseven.untis.core.data.mapper

import com.sapuseven.untis.core.api.mobile.model.untis.classreg.Exam
import com.sapuseven.untis.core.api.mobile.model.untis.timetable.PeriodExam
import com.sapuseven.untis.core.model.timetable.Element
import com.sapuseven.untis.core.model.timetable.ElementKey
import com.sapuseven.untis.core.model.timetable.ElementType
import com.sapuseven.untis.core.model.timetable.Exam as DomainExam

// TODO
internal fun PeriodExam.toDomain() = DomainExam(
	id = id,
	type = examtype,
	name = name,
	text = text,
	startDateTime = TODO(),
	endDateTime = TODO(),
	subject = TODO(),
	classes = TODO(),
	rooms = TODO(),
	teachers = TODO(),
)

internal fun Exam.toDomain(
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
)

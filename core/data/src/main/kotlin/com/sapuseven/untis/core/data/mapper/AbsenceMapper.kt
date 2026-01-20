package com.sapuseven.untis.core.data.mapper

import com.sapuseven.untis.core.api.mobile.model.untis.absence.Excuse
import com.sapuseven.untis.core.api.mobile.model.untis.absence.StudentAbsence
import com.sapuseven.untis.core.model.timetable.Element
import com.sapuseven.untis.core.model.timetable.ElementKey
import com.sapuseven.untis.core.model.timetable.ElementType
import com.sapuseven.untis.core.model.absences.Absence as DomainAbsence
import com.sapuseven.untis.core.model.absences.Excuse as DomainExcuse

internal fun StudentAbsence.toDomain(
	allElements: Map<ElementKey, Element>,
	students: Map<Long, Element>,
) = DomainAbsence(
	id = id,
	text = text,
	absentStudent = students[studentId],
	absentClass = allElements[(ElementKey(klasseId, ElementType.CLASS))],
	startDateTime = startDateTime,
	endDateTime = endDateTime,
	owner = owner,
	excuse = excuse?.toDomain(excused),
	absenceReason = absenceReason,
)

internal fun Excuse.toDomain(
	excused: Boolean
) = DomainExcuse(
	id = id,
	text = text,
	excused = excused,
	active = true,
	excusedDate = date,
)

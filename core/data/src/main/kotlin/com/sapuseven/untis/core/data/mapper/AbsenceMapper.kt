package com.sapuseven.untis.core.data.mapper

import com.sapuseven.untis.core.api.model.untis.absence.Excuse
import com.sapuseven.untis.core.api.model.untis.absence.StudentAbsence
import com.sapuseven.untis.core.model.absences.Absence as DomainAbsence
import com.sapuseven.untis.core.model.absences.Excuse as DomainExcuse

internal fun StudentAbsence.toDomain() = DomainAbsence(
	id = id,
	studentId = studentId,
	klasseId = klasseId,
	startDateTime = startDateTime,
	endDateTime = endDateTime,
	owner = owner,
	excused = excused,
	excuse = excuse?.toDomain(),
	absenceReason = absenceReason,
	text = text,
)

internal fun Excuse.toDomain() = DomainExcuse(
	id = id,
	text = text,
	date = date,
)

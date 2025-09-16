package com.sapuseven.untis.core.data.mapper

import com.sapuseven.untis.core.api.model.untis.absence.StudentAbsence
import com.sapuseven.untis.core.model.timetable.PeriodDetails

internal fun com.sapuseven.untis.core.api.model.untis.timetable.PeriodData.toDomain() = PeriodDetails(
	absenceChecked = absenceChecked,
	studentIds = studentIds ?: emptyList(),
	absences = absences?.map(StudentAbsence::toDomain) ?: emptyList(),
)

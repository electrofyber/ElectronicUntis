package com.sapuseven.untis.core.data.mapper

import com.sapuseven.untis.core.api.model.untis.timetable.PeriodData
import com.sapuseven.untis.core.model.timetable.Element
import com.sapuseven.untis.core.model.timetable.ElementKey
import com.sapuseven.untis.core.model.timetable.PeriodDetails as DomainPeriodDetails

internal fun PeriodData.toDomain(
	allElements: Map<ElementKey, Element>,
	students: Map<Long, Element>,
) = DomainPeriodDetails(
	absenceChecked = absenceChecked,
	students = studentIds?.mapNotNull { students[it] } ?: emptyList(),
	absences = absences?.map { it.toDomain(allElements, students) } ?: emptyList(),
)

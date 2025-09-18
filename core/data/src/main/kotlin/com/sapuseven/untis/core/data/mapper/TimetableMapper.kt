package com.sapuseven.untis.core.data.mapper

import com.sapuseven.untis.core.api.model.untis.Timetable
import com.sapuseven.untis.core.model.timetable.Element
import com.sapuseven.untis.core.model.timetable.ElementKey
import kotlinx.datetime.Clock
import com.sapuseven.untis.core.model.timetable.Timetable as DomainTimetable

internal fun Timetable.toDomain(
	allElements: Map<ElementKey, Element>,
	clock: Clock = Clock.System
) = DomainTimetable(
	startDate = displayableStartDate,
	endDate = displayableEndDate,
	timestamp = clock.now(),
	periods = periods.map { it.toDomain(allElements) }
)

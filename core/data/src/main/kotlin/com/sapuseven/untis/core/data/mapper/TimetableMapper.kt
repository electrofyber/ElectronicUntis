package com.sapuseven.untis.core.data.mapper

import com.sapuseven.untis.core.database.entity.ElementEntity
import com.sapuseven.untis.core.model.ElementType
import com.sapuseven.untis.core.model.Timetable
import kotlinx.datetime.Clock

internal fun com.sapuseven.untis.core.api.model.untis.Timetable.toDomain(
	allElements: Map<ElementType, List<ElementEntity>>,
	clock: Clock = Clock.System
) = Timetable(
	startDate = displayableStartDate,
	endDate = displayableEndDate,
	timestamp = clock.now(),
	periods = periods.map { it.toDomain(allElements) }
)

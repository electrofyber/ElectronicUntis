package com.sapuseven.untis.core.data.mapper

import com.sapuseven.untis.core.api.model.untis.timetable.Period
import com.sapuseven.untis.core.model.Timetable
import java.time.Instant

internal fun com.sapuseven.untis.core.api.model.untis.Timetable.toDomain() = Timetable(
	startDate = displayableStartDate,
	endDate = displayableEndDate,
	timestamp = Instant.now(),
	periods = periods.map(Period::toDomain)
)

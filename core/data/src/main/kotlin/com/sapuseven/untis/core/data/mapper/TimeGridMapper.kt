package com.sapuseven.untis.core.data.mapper

import com.sapuseven.untis.core.api.model.untis.masterdata.TimeGrid
import com.sapuseven.untis.core.api.model.untis.masterdata.timegrid.Day
import com.sapuseven.untis.core.api.model.untis.masterdata.timegrid.Unit
import com.sapuseven.untis.core.model.timetable.TimeGrid as DomainTimeGrid
import com.sapuseven.untis.core.model.timetable.TimeGridDay as DomainTimeGridDay
import com.sapuseven.untis.core.model.timetable.TimeGridUnit as DomainTimeGridUnit

internal fun TimeGrid.toDomain() = DomainTimeGrid(
	days = days.map { it.toDomain() }
)

internal fun Day.toDomain() = DomainTimeGridDay(
	dayOfWeek = day,
	units = units.map { it.toDomain() }
)

internal fun Unit.toDomain() = DomainTimeGridUnit(
	label = label,
	startTime = startTime,
	endTime = endTime,
)

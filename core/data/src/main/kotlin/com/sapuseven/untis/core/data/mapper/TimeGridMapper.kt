package com.sapuseven.untis.core.data.mapper

import com.sapuseven.untis.core.model.timetable.TimeGrid
import com.sapuseven.untis.core.model.timetable.TimeGridDay
import com.sapuseven.untis.core.model.timetable.TimeGridUnit

internal fun com.sapuseven.untis.core.api.model.untis.masterdata.TimeGrid.toDomain() = TimeGrid(
	days = days.map { it.toDomain() }
)

internal fun com.sapuseven.untis.core.api.model.untis.masterdata.timegrid.Day.toDomain() = TimeGridDay(
	dayOfWeek = day,
	units = units.map { it.toDomain() }
)

internal fun com.sapuseven.untis.core.api.model.untis.masterdata.timegrid.Unit.toDomain() = TimeGridUnit(
	label = label,
	startTime = startTime,
	endTime = endTime,
)

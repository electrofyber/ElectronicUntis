package com.sapuseven.untis.core.api.mobile.model.untis.masterdata.timegrid

import com.sapuseven.untis.core.api.serializer.DayOfWeekSerializer
import kotlinx.datetime.DayOfWeek
import kotlinx.serialization.Serializable

@Serializable
data class Day(
	@Serializable(with = DayOfWeekSerializer::class)
	val day: DayOfWeek,
	val units: List<Unit>
)

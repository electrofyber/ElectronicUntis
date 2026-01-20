package com.sapuseven.untis.core.api.mobile.model.untis.masterdata

import com.sapuseven.untis.core.api.mobile.model.untis.masterdata.timegrid.Day
import kotlinx.serialization.Serializable

@Serializable
data class TimeGrid(
	val days: List<Day>
)

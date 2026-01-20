package com.sapuseven.untis.core.api.mobile.model.untis.masterdata.timegrid

import com.sapuseven.untis.core.api.serializer.Time
import kotlinx.serialization.Serializable

@Serializable
data class Unit(
	val label: String,
	val startTime: Time,
	val endTime: Time
)

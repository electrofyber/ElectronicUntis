package com.sapuseven.untis.core.api.mobile.model.untis.masterdata

import com.sapuseven.untis.core.api.serializer.Date
import kotlinx.serialization.Serializable

@Serializable
data class SchoolYear(
	val id: Long,
	val name: String,
	val startDate: Date,
	val endDate: Date
)

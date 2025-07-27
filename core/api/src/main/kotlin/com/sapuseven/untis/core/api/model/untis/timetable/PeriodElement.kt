package com.sapuseven.untis.core.api.model.untis.timetable

import com.sapuseven.untis.core.api.model.untis.enumeration.ElementType
import kotlinx.serialization.Serializable

@Serializable
data class PeriodElement(
	val type: ElementType,
	val id: Long,
	val orgId: Long = id
)

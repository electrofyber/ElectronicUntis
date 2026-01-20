package com.sapuseven.untis.core.api.mobile.model.untis.classreg

import com.sapuseven.untis.core.api.serializer.Time
import kotlinx.serialization.Serializable

@Serializable
data class Invigilator(
	val id: Long,
	val startTime: Time,
	val endTime: Time
)

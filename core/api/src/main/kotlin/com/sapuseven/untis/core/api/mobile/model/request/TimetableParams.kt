package com.sapuseven.untis.core.api.mobile.model.request

import com.sapuseven.untis.core.api.mobile.model.untis.Auth
import com.sapuseven.untis.core.api.serializer.Date
import kotlinx.serialization.Serializable

@Serializable
data class TimetableParams(
	val id: Long,
	val type: String,
	val startDate: Date,
	val endDate: Date,
	val masterDataTimestamp: Long, // TODO: Try how the response behaves depending on changes to this value
	val timetableTimestamp: Long,
	val timetableTimestamps: List<Long>,
	val auth: Auth
) : BaseParams()

package com.sapuseven.untis.core.api.mobile.model.request

import com.sapuseven.untis.core.api.mobile.model.untis.Auth
import com.sapuseven.untis.core.api.serializer.Date
import kotlinx.serialization.Serializable

@Serializable
data class StudentAbsencesParams(
	val startDate: Date,
	val endDate: Date,
	val includeExcused: Boolean,
	val includeUnExcused: Boolean,
	val auth: Auth
) : BaseParams()

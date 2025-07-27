package com.sapuseven.untis.core.api.model.request

import com.sapuseven.untis.core.api.model.untis.Auth
import com.sapuseven.untis.core.api.serializer.Date
import kotlinx.serialization.Serializable


@Serializable
data class OfficeHoursParams(
	val klasseId: Long,
	val startDate: Date,
	val auth: Auth
) : BaseParams()

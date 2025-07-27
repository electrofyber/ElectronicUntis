package com.sapuseven.untis.core.api.model.request

import com.sapuseven.untis.core.api.model.untis.Auth
import com.sapuseven.untis.core.api.serializer.Date
import kotlinx.serialization.Serializable

@Serializable
data class PeriodDataParams(
	val ttIds: Set<Long>,
	val auth: Auth
) : BaseParams()

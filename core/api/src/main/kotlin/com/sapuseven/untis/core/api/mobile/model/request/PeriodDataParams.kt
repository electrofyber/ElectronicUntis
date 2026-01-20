package com.sapuseven.untis.core.api.mobile.model.request

import com.sapuseven.untis.core.api.mobile.model.untis.Auth
import com.sapuseven.untis.core.api.serializer.Date
import kotlinx.serialization.Serializable

@Serializable
data class PeriodDataParams(
	val ttIds: Set<Long>,
	val auth: Auth
) : BaseParams()

package com.sapuseven.untis.core.api.mobile.model.request

import com.sapuseven.untis.core.api.mobile.model.untis.Auth
import com.sapuseven.untis.core.api.mobile.model.untis.enumeration.ElementType
import com.sapuseven.untis.core.api.serializer.Date
import kotlinx.serialization.Serializable

@Serializable
data class ExamsParams(
	val id: Long,
	val type: ElementType,
	val startDate: Date,
	val endDate: Date,
	val auth: Auth
) : BaseParams()

package com.sapuseven.untis.core.api.model.untis.absence

import com.sapuseven.untis.core.api.serializer.Date
import kotlinx.serialization.Serializable

@Serializable
data class Excuse(
	val id: Long,
	val excuseStatusId: Long,
	val text: String?,
	val number: Long,
	val date: Date? = null
)

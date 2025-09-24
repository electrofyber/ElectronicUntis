package com.sapuseven.untis.core.model.timetable

import kotlinx.serialization.Serializable

@Serializable
data class Attachment(
	val id: Long,
	val name: String,
	val url: String,
)

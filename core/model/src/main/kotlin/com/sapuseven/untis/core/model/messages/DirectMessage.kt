package com.sapuseven.untis.core.model.messages

import com.sapuseven.untis.core.model.timetable.Attachment
import kotlinx.serialization.Serializable

@Serializable
data class DirectMessage(
	val id: Long,
	val subject: String?,
	val body: String?,
	val attachments: List<Attachment> = emptyList()
)

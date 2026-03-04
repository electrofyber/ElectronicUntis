package com.sapuseven.untis.core.model.messages

import com.sapuseven.untis.core.model.timetable.Attachment
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class DirectMessage(
	val id: Long,
	val timestamp: LocalDateTime?,
	val subject: String?,
	val body: String?,
	val attachments: List<Attachment> = emptyList(),
	val unread: Boolean,
	val sender: MessageParticipant?,
	val recipients: List<MessageParticipant>?,
)

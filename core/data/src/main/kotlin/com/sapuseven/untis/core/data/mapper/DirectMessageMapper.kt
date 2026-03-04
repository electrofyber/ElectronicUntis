package com.sapuseven.untis.core.data.mapper

import com.sapuseven.untis.core.api.rest.model.Message
import com.sapuseven.untis.core.api.rest.model.Recipient
import com.sapuseven.untis.core.api.rest.model.Sender
import com.sapuseven.untis.core.model.messages.DirectMessage as DomainDirectMessage
import com.sapuseven.untis.core.model.messages.MessageParticipant as DomainMessageParticipant

internal fun Message.toDomain() = DomainDirectMessage(
	id = id,
	timestamp = sentDateTime,
	subject = subject,
	body = content ?: contentPreview,
	attachments = emptyList(), // TODO attachments?.map { it.toDomain() } ?: emptyList()
	unread = isMessageRead == false,
	sender = sender?.toDomain(),
	recipients = recipients?.map { it.toDomain() } ?: emptyList(),
)

internal fun Sender.toDomain() = DomainMessageParticipant.User(
	id = userId ?: -1,
	name = displayName,
	avatarUrl = imageUrl,
)

internal fun Recipient.toDomain() = when (type) {
	Recipient.Type.GROUP -> DomainMessageParticipant.Group(
		id = groupId ?: -1,
		name = displayName,
		avatarUrl = imageUrl,
	)
	else -> DomainMessageParticipant.User(
		id = userId ?: -1,
		name = displayName,
		avatarUrl = imageUrl,
	)
}

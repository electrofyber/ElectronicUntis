package com.sapuseven.untis.core.data.mapper

import com.sapuseven.untis.core.api.rest.model.Message
import com.sapuseven.untis.core.model.messages.DirectMessage as DomainDirectMessage

internal fun Message.toDomain() = DomainDirectMessage(
	id = id,
	subject = subject,
	body = content,
	attachments = emptyList() // TODO attachments?.map { it.toDomain() } ?: emptyList()
)

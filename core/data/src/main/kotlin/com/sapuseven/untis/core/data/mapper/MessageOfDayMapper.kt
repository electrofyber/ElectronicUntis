package com.sapuseven.untis.core.data.mapper

import com.sapuseven.untis.core.api.mobile.model.untis.Attachment
import com.sapuseven.untis.core.api.mobile.model.untis.MessageOfDay
import com.sapuseven.untis.core.model.messages.MessageOfDay as DomainMessageOfDay

internal fun MessageOfDay.toDomain() = DomainMessageOfDay(
	id = id,
	subject = subject,
	body = body,
	attachments = attachments.map(Attachment::toDomain),
)

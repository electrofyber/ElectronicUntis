package com.sapuseven.untis.core.data.mapper

import com.sapuseven.untis.core.model.Attachment

internal fun com.sapuseven.untis.core.api.model.untis.Attachment.toDomain() = Attachment(
	id = id,
	name = name,
	url = url,
)

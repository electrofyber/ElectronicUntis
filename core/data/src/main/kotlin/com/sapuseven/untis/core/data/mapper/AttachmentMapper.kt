package com.sapuseven.untis.core.data.mapper

import com.sapuseven.untis.core.model.timetable.Attachment

internal fun com.sapuseven.untis.core.api.mobile.model.untis.Attachment.toDomain() = Attachment(
	id = id,
	name = name,
	url = url,
)

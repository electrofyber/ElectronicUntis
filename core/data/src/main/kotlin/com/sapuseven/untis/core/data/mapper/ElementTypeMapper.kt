package com.sapuseven.untis.core.data.mapper

import com.sapuseven.untis.core.model.ElementType

internal fun com.sapuseven.untis.core.api.model.untis.enumeration.ElementType.toDomain() =
	ElementType.valueOf(name)

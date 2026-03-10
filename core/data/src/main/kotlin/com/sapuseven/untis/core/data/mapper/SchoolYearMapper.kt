package com.sapuseven.untis.core.data.mapper

import com.sapuseven.untis.core.database.entity.SchoolYearEntity
import com.sapuseven.untis.core.model.masterdata.SchoolYear

internal fun SchoolYearEntity.toDomain() = SchoolYear(
	id = id,
	name = name,
	startDate = startDate,
	endDate = endDate
)


package com.sapuseven.untis.core.data.mapper

import com.sapuseven.untis.core.database.entity.ExcuseStatusEntity
import com.sapuseven.untis.core.model.absences.Excuse

// Map Entity -> Domain
internal fun ExcuseStatusEntity.toDomain() = Excuse(
	id = id,
	text = longName,
	excused = excused,
	active = active,
)

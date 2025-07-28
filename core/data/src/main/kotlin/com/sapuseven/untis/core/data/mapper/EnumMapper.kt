package com.sapuseven.untis.core.data.mapper

import com.sapuseven.untis.core.model.ElementType
import com.sapuseven.untis.core.model.PeriodRight
import com.sapuseven.untis.core.model.PeriodState

internal fun com.sapuseven.untis.core.api.model.untis.enumeration.ElementType.toDomain() =
	ElementType.valueOf(name)

internal fun com.sapuseven.untis.core.api.model.untis.enumeration.PeriodRight.toDomain() =
	PeriodRight.valueOf(name)

internal fun com.sapuseven.untis.core.api.model.untis.enumeration.PeriodState.toDomain() =
	PeriodState.valueOf(name)

package com.sapuseven.untis.core.data.mapper

import com.sapuseven.untis.core.api.mobile.model.untis.enumeration.ElementType
import com.sapuseven.untis.core.api.mobile.model.untis.enumeration.PeriodRight
import com.sapuseven.untis.core.api.mobile.model.untis.enumeration.PeriodState
import com.sapuseven.untis.core.api.mobile.model.untis.enumeration.Right
import crocodile8.universal_cache.FromCache
import com.sapuseven.untis.core.domain.cache.FromCache as DomainFromCache
import com.sapuseven.untis.core.model.timetable.ElementType as DomainElementType
import com.sapuseven.untis.core.model.timetable.PeriodRight as DomainPeriodRight
import com.sapuseven.untis.core.model.timetable.PeriodState as DomainPeriodState
import com.sapuseven.untis.core.model.user.UserRight as DomainUserRight

internal fun ElementType.toDomain() = DomainElementType.valueOf(name)

internal fun DomainElementType.toData() = ElementType.valueOf(name)

internal fun PeriodRight.toDomain() = DomainPeriodRight.valueOf(name)

internal fun PeriodState.toDomain() = DomainPeriodState.valueOf(name)

internal fun Right.toDomain() = DomainUserRight.valueOf(name)

internal fun FromCache.toDomain() = DomainFromCache.valueOf(name)

internal fun DomainFromCache.toData() = FromCache.valueOf(name)

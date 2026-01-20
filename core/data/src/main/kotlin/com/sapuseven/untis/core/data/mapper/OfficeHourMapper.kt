package com.sapuseven.untis.core.data.mapper

import com.sapuseven.untis.core.api.mobile.model.untis.timetable.OfficeHour
import com.sapuseven.untis.core.model.timetable.Element
import com.sapuseven.untis.core.model.timetable.ElementKey
import com.sapuseven.untis.core.model.timetable.ElementType
import com.sapuseven.untis.core.model.officehours.OfficeHour as DomainOfficeHour
import com.sapuseven.untis.core.model.officehours.OfficeHourRegistrationInfo as DomainOfficeHourRegistrationInfo

internal fun OfficeHour.toDomain(
	allElements: Map<ElementKey, Element>
) = DomainOfficeHour(
	id = id,
	teacher = allElements[ElementKey(teacherId, ElementType.TEACHER)]!!, // TODO handle null
	startDateTime = startDateTime,
	endDateTime = endDateTime,
	displayNameRooms = displayNameRooms,
	registrationInfo = mapRegistrationInfo(),
)

private fun OfficeHour.mapRegistrationInfo() = DomainOfficeHourRegistrationInfo(
	registrationPossible = registrationPossible,
	registered = registered,
	email = email,
	phone = phone,
)

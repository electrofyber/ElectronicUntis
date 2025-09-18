package com.sapuseven.untis.core.domain.timetable

import com.sapuseven.untis.core.model.timetable.Element
import com.sapuseven.untis.core.model.timetable.Period.Companion.ELEMENT_NAME_SEPARATOR

fun List<Element>.toShortString(): String =
	joinToString(ELEMENT_NAME_SEPARATOR) { it.shortName }

fun List<Element>.toLongString(): String =
	joinToString(ELEMENT_NAME_SEPARATOR) { it.shortName }

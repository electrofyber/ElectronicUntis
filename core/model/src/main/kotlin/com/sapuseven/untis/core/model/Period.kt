package com.sapuseven.untis.core.model

/**
 * Represents a period in a timetable.
 *
 * It can be viewed as a lesson and contains several [Element]s.
 * Information that is relevant for a whole lesson is stored in this object,
 * for example lesson topic and homework.
 *
 * @see Timetable
 * @see Element
 */
data class Period(
	val id: Long,
)

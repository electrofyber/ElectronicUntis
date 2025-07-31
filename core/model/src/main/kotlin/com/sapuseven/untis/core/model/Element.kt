package com.sapuseven.untis.core.model

/**
 * An Element represents an element inside a [Period], which can be a class, teacher, subject, or room.
 *
 * @property id The unique identifier for the element.
 * @property type The type of the element.
 * @property shortName The name of the element.
 * @property longName The name of the element.
 * @property foreColor The suggested foreground color for the element, if any.
 * @property backColor The suggested background color for the element, if any.
 * @property replaced Indicates whether this element was replaced by another element of the same type.
 * This is typically used for cases like teacher substitutions or room changes.
 * @property timetableAllowed Indicates whether or not it is allowed to view the timetable of this element.
 * @see Timetable
 * @see Period
 */
data class Element(
	val id: Long,
	val type: ElementType,
	val shortName: String,
	val longName: String,
	val foreColor: String? = null,
	val backColor: String? = null,
	val replaced: Boolean = false,
	val timetableAllowed: Boolean,
) {
	companion object {
		fun personal(id: Long, type: ElementType, name: String) = Element(
			id = id,
			type = type,
			shortName = name,
			longName = name,
			timetableAllowed = true
		)
	}
}

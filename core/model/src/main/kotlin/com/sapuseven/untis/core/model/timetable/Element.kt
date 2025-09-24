package com.sapuseven.untis.core.model.timetable

import kotlinx.serialization.Serializable

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
@Serializable
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
		/**
		 * Initialize a basic Element when you only have an ElementKey.
		 * Only use this for data transfer and if you are sure the element data is never visible to the user!
		 */
		fun basic(key: ElementKey) = Element(
			id = key.id,
			type = key.type,
			shortName = "",
			longName = "",
			timetableAllowed = true
		)

		fun personal(id: Long, type: ElementType, name: String) = Element(
			id = id,
			type = type,
			shortName = name,
			longName = name,
			timetableAllowed = true
		)
	}
}

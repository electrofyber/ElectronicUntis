package com.sapuseven.untis.core.model.user

import com.sapuseven.untis.core.model.timetable.Element
import com.sapuseven.untis.core.model.timetable.School
import com.sapuseven.untis.core.model.timetable.TimeGrid

/**
 * Represents a user profile in the app.
 *
 * Every user has it's own data and preferences.
 * There may be multiple users for the same person.
 *
 * @property id A unique identifier for every user instance. Must be positive. `0` indicates the user has not yet been persisted.
 * @property name The name of the element this user corresponds to. Should only be used if referring to the actual element of the user.
 * @property displayName A custom display name for this user profile. Can be freely customized. Should be used when referring to this user profile.
 * @property school Information about the user's school. Contains API information.
 * @property credentials The stored credentials for this user. Used for authenticating with the API. `null` for anonymous access.
 * @property element The element corresponding to this user. Used for personal timetable. Usually [com.sapuseven.untis.core.model.timetable.ElementType.STUDENT]. `null` if anonymous or no element is associated with this user.
 */
data class User(
	val id: Long,
	val name: String,
	val displayName: String,
	val school: School,
	val credentials: UserCredentials? = null,
	val element: Element? = null,
	val rights: List<UserRight>,
	val timeGrid: TimeGrid
) {
	val isAnonymous: Boolean
		get() = credentials == null

	fun hasRight(right: UserRight) = rights.contains(right)

	init {
		require(id >= 0)
	}
}

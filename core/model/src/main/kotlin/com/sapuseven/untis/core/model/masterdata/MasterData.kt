package com.sapuseven.untis.core.model.masterdata

/**
 * MasterData holds all the master data information such as students, teachers, rooms, subjects, and classes.
 *
 * This data is only fetched once at login and provides a reference for other data models.
 *
 * @property timestamp The timestamp of when the master data was last updated.
 */
data class MasterData(
	val timestamp: Long = 0,
	// TODO
)

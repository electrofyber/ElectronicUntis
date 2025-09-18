package com.sapuseven.untis.core.model.masterdata

import com.sapuseven.untis.core.model.timetable.Element


/**
 * MasterData holds all the master data information such as students, teachers, rooms, subjects, and classes.
 *
 * This data is only fetched once at login and provides a reference for other data models.
 *
 * @property timestamp The timestamp of when the master data was last updated.
 */
data class MasterData(
	val timestamp: Long = 0,
	val classes: List<Element>,
	val rooms: List<Element>,
	val subjects: List<Element>,
	val teachers: List<Element>,
	// TODO
)

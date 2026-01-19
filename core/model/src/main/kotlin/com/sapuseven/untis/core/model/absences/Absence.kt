package com.sapuseven.untis.core.model.absences

import com.sapuseven.untis.core.model.timetable.Element
import com.sapuseven.untis.core.model.timetable.ElementType
import kotlinx.datetime.LocalDateTime

data class Absence(
	/**
	 * Unique identifier of the absence.
	 */
	val id: Long,

	/**
	 * Additional text/details about the absence.
	 */
	val text: String,

	/**
	 * The student associated with the absence.
	 * The element type is always [ElementType.STUDENT].
	 */
	val absentStudent: Element?,

	/*
	 * The class of the student associated with the absence.
	 * Since a student may be part of multiple classes, this field indicates
	 * the specific class context in which the absence was recorded.
	 * The element type is always [ElementType.CLASS].
	 */
	val absentClass: Element?,

	/**
	 * The start date and time of this absence.
	 */
	val startDateTime: LocalDateTime,

	/**
	 * The end date and time of this absence.
	 */
	val endDateTime: LocalDateTime,

	/**
	 * Whether or not the absence is owned by the current user.
	 */
	val owner: Boolean,

	/**
	 * The reason for the absence.
	 */
	val absenceReason: String,

	/**
	 * The excuse associated with this absence, if any.
	 */
	val excuse: Excuse?,
)

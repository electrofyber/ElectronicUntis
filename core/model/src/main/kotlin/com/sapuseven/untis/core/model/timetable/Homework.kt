package com.sapuseven.untis.core.model.timetable

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class Homework(
	/**
	 * The unique id of this homework entry.
	 */
	val id: Long,

	// Not yet implemented
	//val lessonId: Long,

	/**
	 * The instructional text of this homework entry.
	 */
	val text: String,

	/**
	 * The subject associated with this homework entry.
	 * The element type is always [ElementType.SUBJECT].
	 */
	val subject: Element? = null,

	/**
	 * The start date of this homework entry.
	 */
	val startDate: LocalDate,

	/**
	 * The end date of this homework entry.
	 */
	val endDate: LocalDate,

	/**
	 * The attachments associated with this homework entry.
	 */
	val attachments: List<Attachment>,

	/**
	 * Whether this homework entry is marked as completed.
	 */
	val completed: Boolean
)

package com.sapuseven.untis.core.model.timetable

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class Exam(
	/**
	 * The unique id of this exam.
	 */
	val id: Long,

	/**
	 * The exam type (most likely a custom string).
	 */
	val type: String?,

	/**
	 * Display name of the exam.
	 */
	val name: String?,

	/**
	 * Display text (additional details) of the exam.
	 */
	val text: String?,

	/**
	 * The start date and time of this exam.
	 */
	val startDateTime: LocalDateTime,

	/**
	 * The end date and time of this exam.
	 */
	val endDateTime: LocalDateTime,

	/**
	 * The subject associated with this exam.
	 * The element type is always [ElementType.SUBJECT].
	 */
	val subject: Element? = null,

	/**
	 * The classes associated with this exam.
	 * The element type is always [ElementType.CLASS].
	 */
	val classes: List<Element> = emptyList(),

	/**
	 * The rooms associated with this exam.
	 * The element type is always [ElementType.ROOM].
	 */
	val rooms: List<Element> = emptyList(),

	/**
	 * The teachers associated with this exam.
	 * The element type is always [ElementType.TEACHER].
	 */
	val teachers: List<Element> = emptyList(),

	// Not yet implemented
	//val invigilators: List<Invigilator> = emptyList(),
)

package com.sapuseven.untis.core.model

import java.time.LocalDateTime

/**
 * Represents a period in a [Timetable].
 *
 * It can be viewed as a lesson and contains several [Element]s.
 * Information that is relevant for a whole lesson is stored in this object,
 * for example lesson topic and homework.
 *
 * There can be multiple periods for the same lesson, for example:
 * Subject "English" is taught on Monday and Wednesday.
 * The two periods for this lesson would have the same [lessonId], but a different [id].
 *
 * @property id The id of this period. Can be used to uniquely identify this period in the timetable.
 * @property lessonId The lesson id of this period. Can be used to group multiple periods of the same lesson together.
 * @property startDateTime The start date and time of this period. Local time without timezone.
 * @property endDateTime The end date and time of this period. Local time without timezone.
 * @property infoTexts Additional info texts of this period. It contains information about the lesson, substitution or the period in general.
 * @property rights The rights of the current user for this period, generally a list of read/write permissions.
 * @property states The states of this period, for example to indicate if the period is cancelled or a substitute lesson.
 * @property homeworks The homeworks associated with this period, if any.
 * @property exam The exam associated with this period, if any.
 * @property onlinePeriod Indicates if this period is an online period, i.e. via video conferencing. `null` if not specified.
 * @property onlinePeriodLink The link to the online period, if [onlinePeriod] is `true`.
 *
 * @see Timetable
 * @see Element
 */
data class Period(
	val id: Long,
	val lessonId: Long,
	var startDateTime: LocalDateTime,
	var endDateTime: LocalDateTime,
	val foreColor: String,
	val backColor: String,
	val innerForeColor: String,
	val innerBackColor: String,
	val infoTexts: List<PeriodInfoText> = emptyList(),
	val elements: List<Element> = emptyList(),
	val rights: List<PeriodRight> = emptyList(),
	val states: List<PeriodState> = emptyList(),
	val attachments: List<Attachment> = emptyList(),
	val homeworks: List<Homework> = emptyList(),
	val exam: Exam? = null,
	val onlinePeriod: Boolean? = null,
	val onlinePeriodLink: String? = null,
)

package com.sapuseven.untis.core.model.timetable

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

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
@Serializable
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
) {
	@Transient val classes = elements.filter { it.type == ElementType.CLASS }
	@Transient val teachers = elements.filter { it.type == ElementType.TEACHER }
	@Transient val subjects = elements.filter { it.type == ElementType.SUBJECT }
	@Transient val rooms = elements.filter { it.type == ElementType.ROOM }

	@Transient var forceIrregular = false

	companion object {
		const val ELEMENT_NAME_SEPARATOR = ", "
	}

	fun isCancelled(): Boolean = states.contains(PeriodState.CANCELLED)

	fun isIrregular(): Boolean = forceIrregular || states.contains(PeriodState.IRREGULAR)

	fun isExam(): Boolean = states.contains(PeriodState.EXAM)

	/**
	 * Checks if this period equals another period, ignoring the start and end time, as well as the id.
	 *
	 * This can be used to check if two periods represent the same lesson, even if they are at different times.
	 *
	 * @param other The other period to compare with.
	 * @return `true` if the periods are equal ignoring time and id, `false` otherwise.
	 */
	fun equalsIgnoreTime(other: Period): Boolean {
		if (this === other) return true

		if (lessonId != other.lessonId) return false
		if (onlinePeriod != other.onlinePeriod) return false
		if (foreColor != other.foreColor) return false
		if (backColor != other.backColor) return false
		if (innerForeColor != other.innerForeColor) return false
		if (innerBackColor != other.innerBackColor) return false
		if (infoTexts != other.infoTexts) return false
		if (elements != other.elements) return false
		if (rights != other.rights) return false
		if (states != other.states) return false
		if (attachments != other.attachments) return false
		if (homeworks != other.homeworks) return false
		if (exam != other.exam) return false
		if (onlinePeriodLink != other.onlinePeriodLink) return false

		return true
	}

	/*operator fun plus(other: Period) =
		copy(
			elements = this.elements + other.elements,
		)*/
}

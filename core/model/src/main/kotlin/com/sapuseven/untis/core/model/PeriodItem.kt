package com.sapuseven.untis.core.model

/**
 * A timetable period represents a single "slot" in the timetable.
 *
 * A [PeriodItem] contains all subjects, classes, teachers, and rooms that occur during that period.
 * Every subject, class, teacher, or room is an element of the period.
 */
/*class PeriodItem(
	@Transient private var elements: List<PeriodElementEntity> = emptyList(),
	val originalPeriod: Period
) {
	@Transient val classes = elements.filter { it.entity.getType() == ElementType.CLASS }
	@Transient val teachers = elements.filter { it.entity.getType() == ElementType.TEACHER }
	@Transient val subjects = elements.filter { it.entity.getType() == ElementType.SUBJECT }
	@Transient val rooms = elements.filter { it.entity.getType() == ElementType.ROOM }

	var forceIrregular = false

	companion object {
		const val ELEMENT_NAME_SEPARATOR = ", "
	}

	fun isCancelled(): Boolean = originalPeriod.`is`.contains(PeriodState.CANCELLED)

	fun isIrregular(): Boolean = forceIrregular || originalPeriod.`is`.contains(PeriodState.IRREGULAR)

	fun isExam(): Boolean = originalPeriod.`is`.contains(PeriodState.EXAM)

	fun equalsIgnoreTime(other: PeriodItem) =
		originalPeriod.equalsIgnoreTime(other.originalPeriod)

	operator fun plus(other: PeriodItem) =
		PeriodItem(
			elements = this.elements + other.elements,
			originalPeriod = originalPeriod
		)
}

fun List<PeriodElementEntity>.toShortString(): String =
	joinToString(ELEMENT_NAME_SEPARATOR) { it.entity.getShortName() }

fun List<PeriodElementEntity>.toLongString(): String =
	joinToString(ELEMENT_NAME_SEPARATOR) { it.entity.getShortName() }

fun List<PeriodElementEntity>.getShortAnnotatedString(
	includeReplacements: Boolean = true
): AnnotatedString {
	return buildAnnotatedString {
		forEach {
			if (length > 0) append(ELEMENT_NAME_SEPARATOR)
			append(it.entity.getShortName())

			it.replacementEntity.takeIf { includeReplacements }?.let { replacement ->
				append(ELEMENT_NAME_SEPARATOR)
				withStyle(SpanStyle(textDecoration = TextDecoration.LineThrough)) {
					append(replacement.getShortName())
				}
			}
		}
	}
}*/

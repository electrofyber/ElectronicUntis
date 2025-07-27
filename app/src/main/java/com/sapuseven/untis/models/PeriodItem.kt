package com.sapuseven.untis.models

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import com.sapuseven.untis.core.api.model.untis.enumeration.ElementType
import com.sapuseven.untis.core.api.model.untis.enumeration.PeriodState
import com.sapuseven.untis.core.api.model.untis.timetable.Period
import com.sapuseven.untis.core.api.model.untis.timetable.PeriodElement
import com.sapuseven.untis.models.PeriodItem.Companion.ELEMENT_NAME_SEPARATOR
import com.sapuseven.untis.core.database.entity.ElementEntity
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * A PeriodElementEntity represents an element in a period, which can be a class, teacher, subject, or room.
 *
 * It may also have a replacement entity if the original entity has been replaced or substituted.
 *
 * A timetable period contains multiple elements, each representing a different aspect of the period.
 *
 * @property entity The main element entity for this period element.
 * @property replacementEntity An optional replacement entity for this period element.
 */
data class PeriodElementEntity(
	val entity: ElementEntity,
	val replacementEntity: ElementEntity? = null,
) {
	constructor(allElements: Map<ElementType, List<ElementEntity>>, periodElement: PeriodElement) : this(
		entity = allElements[periodElement.type].orEmpty().firstOrNull { it.id == periodElement.id }
			?: throw IllegalArgumentException("Element with id ${periodElement.id} and type ${periodElement.type} not found"),
		replacementEntity = allElements[periodElement.type].orEmpty().firstOrNull { it.id == periodElement.orgId }
			?.takeIf { periodElement.id != periodElement.orgId && periodElement.orgId != 0L }
	) {
		// Only elements with the same type as the main element are allowed to be replacements
		assert(replacementEntity?.getType()?.equals(entity.getType()) ?: true) {
			"Replacement entity type does not match original entity type: ${replacementEntity?.getType()} != ${entity.getType()}"
		}
	}
}

/**
 * A timetable period represents a single "slot" in the timetable.
 *
 * A [PeriodItem] contains all subjects, classes, teachers, and rooms that occur during that period.
 * Every subject, class, teacher, or room is an element of the period.
 */
@Serializable
class PeriodItem(
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
}

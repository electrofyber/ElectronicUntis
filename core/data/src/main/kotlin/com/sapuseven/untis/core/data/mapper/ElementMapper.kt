package com.sapuseven.untis.core.data.mapper

import com.sapuseven.untis.core.api.model.untis.timetable.PeriodElement
import com.sapuseven.untis.core.database.entity.ElementEntity
import com.sapuseven.untis.core.model.Element
import com.sapuseven.untis.core.model.ElementType

internal fun PeriodElement.toDomainElements(
	allElements: Map<ElementType, List<ElementEntity>>
): List<Element> {
	val elementsOfType = allElements[type.toDomain()].orEmpty()

	val element = elementsOfType.firstOrNull { it.id == id }?.toDomain()
		?: Element(
			id = id,
			type = type.toDomain(),
			shortName = "?",
			longName = "?",
			foreColor = null,
			backColor = null,
			replaced = false,
			timetableAllowed = false
		) // TODO: Log/handle this error (element not found in database), especially in debug builds

	val replacedElement = if (id != orgId && orgId != 0L) {
		elementsOfType.firstOrNull { it.id == orgId }?.toDomain()?.copy(replaced = true)
	} else null

	return listOfNotNull(element, replacedElement)
}

private fun ElementEntity.toDomain(): Element =
	Element(
		id = id,
		type = type,
		shortName = getShortName(),
		longName = getLongName(),
		foreColor = foreColor,
		backColor = backColor,
		replaced = false,
		timetableAllowed = allowed
	)


fun List<Element>.toShortString(): String =
	joinToString(", ") { it.shortName }

fun List<Element>.toLongString(): String =
	joinToString(", ") { it.longName }

/*fun List<Element>.getShortAnnotatedString(
	includeReplacements: Boolean = true
): AnnotatedString {
	return buildAnnotatedString {
		forEach {
			if (length > 0) append(", ")
			append(it.entity.getShortName())

			it.replacementEntity.takeIf { includeReplacements }?.let { replacement ->
				append(", ")
				withStyle(SpanStyle(textDecoration = TextDecoration.LineThrough)) {
					append(replacement.getShortName())
				}
			}
		}
	}
}*/

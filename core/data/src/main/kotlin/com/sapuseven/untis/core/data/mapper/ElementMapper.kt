package com.sapuseven.untis.core.data.mapper

import com.sapuseven.untis.core.api.model.untis.timetable.PeriodElement
import com.sapuseven.untis.core.database.entity.ElementEntity
import com.sapuseven.untis.core.model.timetable.Element as DomainElement
import com.sapuseven.untis.core.model.timetable.ElementType as DomainElementType

internal fun PeriodElement.toDomainElements(
	allElements: Map<DomainElementType, List<ElementEntity>>
): List<DomainElement> {
	val elementsOfType = allElements[type.toDomain()].orEmpty()

	val element = elementsOfType.firstOrNull { it.id == id }?.toDomain()
		?: DomainElement(
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

internal fun ElementEntity.toDomain(): DomainElement =
	DomainElement(
		id = id,
		type = type,
		shortName = getShortName(),
		longName = getLongName(),
		foreColor = foreColor,
		backColor = backColor,
		replaced = false,
		timetableAllowed = allowed
	)


fun List<DomainElement>.toShortString(): String =
	joinToString(", ") { it.shortName }

fun List<DomainElement>.toLongString(): String =
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

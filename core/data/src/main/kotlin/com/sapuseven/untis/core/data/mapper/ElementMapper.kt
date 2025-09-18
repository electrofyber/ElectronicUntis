package com.sapuseven.untis.core.data.mapper

import com.sapuseven.untis.core.api.model.untis.timetable.PeriodElement
import com.sapuseven.untis.core.database.entity.ElementEntity
import com.sapuseven.untis.core.model.timetable.Element
import com.sapuseven.untis.core.model.timetable.ElementKey

internal fun PeriodElement.toDomainElements(
	allElements: Map<ElementKey, Element>
): List<Element> {
	val element = allElements[ElementKey(id, type.toDomain())]
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
		allElements[ElementKey(orgId, type.toDomain())]?.copy(replaced = true)
	} else null

	return listOfNotNull(element, replacedElement)
}

internal fun ElementEntity.toDomain(): Element =
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

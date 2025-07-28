package com.sapuseven.untis.core.data.mapper

import com.sapuseven.untis.core.api.model.untis.enumeration.ElementType
import com.sapuseven.untis.core.api.model.untis.timetable.PeriodElement
import com.sapuseven.untis.core.database.entity.ElementEntity
import com.sapuseven.untis.core.model.Element

internal fun PeriodElement.toDomainElements(
	allElements: Map<ElementType, List<ElementEntity>>
): List<Element> {
	val elementsOfType = allElements[type].orEmpty()

	val element = elementsOfType.firstOrNull { it.id == id }?.toDomain()
		?: throw IllegalArgumentException("Element with id $id and type $type not found")

	val replacedElement = if (id != orgId && orgId != 0L) {
		elementsOfType.firstOrNull { it.id == orgId }?.toDomain()?.copy(replaced = true)
	} else null

	return listOfNotNull(element, replacedElement)
}

private fun ElementEntity.toDomain(): Element =
	Element(
		id = id,
		type = type.toDomain(),
		name = name,
		foreColor = foreColor,
		backColor = backColor,
		replaced = false
	)

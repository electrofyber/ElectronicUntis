package com.sapuseven.untis.core.data.mapper

import com.sapuseven.untis.core.api.model.untis.timetable.Period
import com.sapuseven.untis.core.model.timetable.Element
import com.sapuseven.untis.core.model.timetable.ElementKey
import com.sapuseven.untis.core.model.timetable.InfoTextType
import com.sapuseven.untis.core.model.timetable.PeriodInfoText
import com.sapuseven.untis.core.model.timetable.Period as DomainPeriod

internal fun Period.toDomain(
	allElements: Map<ElementKey, Element>
) = DomainPeriod(
	id = id,
	lessonId = lessonId,
	startDateTime = startDateTime,
	endDateTime = endDateTime,
	foreColor = foreColor,
	backColor = backColor,
	innerForeColor = innerForeColor,
	innerBackColor = innerBackColor,
	infoTexts = listOfNotNull(
		text.lesson.toPeriodInfoTextOrNull(InfoTextType.LESSON_INFO),
		text.substitution.toPeriodInfoTextOrNull(InfoTextType.SUBSTITUTION_INFO),
		text.info.toPeriodInfoTextOrNull(InfoTextType.PERIOD_INFO),
	),
	elements = elements.flatMap { it.toDomainElements(allElements) },
	rights = can.map { it.toDomain() },
	states = `is`.map { it.toDomain() },
	attachments = text.attachments?.map { it.toDomain() } ?: emptyList(),
	homeworks = homeWorks?.map { it.toDomain(allElements) } ?: emptyList(),
	exam = exam?.toDomain(),
	onlinePeriod = isOnlinePeriod,
	onlinePeriodLink = onlinePeriodLink,
)

private fun String.toPeriodInfoTextOrNull(type: InfoTextType) =
	takeIf { it.isNotBlank() }?.let { PeriodInfoText(type, it) }

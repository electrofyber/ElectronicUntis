package com.sapuseven.untis.core.data.mapper

import com.sapuseven.untis.core.database.entity.ElementEntity
import com.sapuseven.untis.core.model.ElementType
import com.sapuseven.untis.core.model.InfoTextType
import com.sapuseven.untis.core.model.Period
import com.sapuseven.untis.core.model.PeriodInfoText

internal fun com.sapuseven.untis.core.api.model.untis.timetable.Period.toDomain(
	allElements: Map<ElementType, List<ElementEntity>>
) = Period(
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
	homeworks = homeWorks?.map { it.toDomain() } ?: emptyList(),
	exam = exam?.toDomain(),
	onlinePeriod = isOnlinePeriod,
	onlinePeriodLink = onlinePeriodLink,
)

private fun String.toPeriodInfoTextOrNull(type: InfoTextType) =
	takeIf { it.isNotBlank() }?.let { PeriodInfoText(type, it) }

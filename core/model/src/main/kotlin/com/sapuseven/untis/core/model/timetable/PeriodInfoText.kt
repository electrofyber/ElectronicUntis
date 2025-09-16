package com.sapuseven.untis.core.model.timetable

data class PeriodInfoText(
	val type: InfoTextType,
	val text: String,
)

enum class InfoTextType {
	LESSON_INFO,
	SUBSTITUTION_INFO,
	PERIOD_INFO,
}

package com.sapuseven.untis.core.model.absences

import kotlinx.datetime.LocalDate

data class Excuse(
	val id: Long,
	val text: String?,
	val date: LocalDate? = null
)

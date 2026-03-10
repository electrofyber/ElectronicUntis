package com.sapuseven.untis.core.model.masterdata

import kotlinx.datetime.LocalDate

data class SchoolYear(
	val id: Long,
	val name: String,
	val startDate: LocalDate,
	val endDate: LocalDate
)


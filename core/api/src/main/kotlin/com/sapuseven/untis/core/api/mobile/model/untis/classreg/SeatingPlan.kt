package com.sapuseven.untis.core.api.mobile.model.untis.classreg

import kotlinx.serialization.Serializable

@Serializable
data class SeatingPlan(
	val id: Long,
	val name: String,
	val numberOfColumns: Int,
	val numberOfRows: Int,
	val students: List<SeatingPlanStudent> = emptyList()
)

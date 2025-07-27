package com.sapuseven.untis.core.api.model.untis.classreg

import kotlinx.serialization.Serializable

@Serializable
data class SeatingPlanStudent(
	val colIndex: Long,
	val rowIndex: Long,
	val studentId: Long
)

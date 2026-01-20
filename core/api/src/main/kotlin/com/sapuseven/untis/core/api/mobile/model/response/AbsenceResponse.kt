package com.sapuseven.untis.core.api.mobile.model.response

import com.sapuseven.untis.core.api.mobile.model.untis.absence.StudentAbsence
import kotlinx.serialization.Serializable

@Serializable
data class CreateImmediateAbsenceResponse(
	val result: CreateImmediateAbsenceResult? = null
) : BaseResponse()

@Serializable
data class CreateImmediateAbsenceResult(
	val absences: List<StudentAbsence>
)

@Serializable
data class DeleteAbsenceResponse(
	val result: DeleteAbsenceResult? = null
) : BaseResponse()

@Serializable
data class DeleteAbsenceResult(
	val success: Boolean
)

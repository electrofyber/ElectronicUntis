package com.sapuseven.untis.core.api.mobile.model.response

import com.sapuseven.untis.core.api.mobile.model.untis.absence.StudentAbsence
import kotlinx.serialization.Serializable

@Serializable
data class StudentAbsencesResponse(
	val result: StudentAbsencesResult? = null
) : BaseResponse()

@Serializable
data class StudentAbsencesResult(
	val absences: List<StudentAbsence>
)

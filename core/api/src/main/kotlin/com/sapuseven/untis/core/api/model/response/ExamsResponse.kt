package com.sapuseven.untis.core.api.model.response

import com.sapuseven.untis.core.api.model.untis.classreg.Exam
import com.sapuseven.untis.core.api.model.untis.enumeration.ElementType
import kotlinx.serialization.Serializable


@Serializable
data class ExamsResponse(
	val result: ExamsResult? = null
) : BaseResponse()

@Serializable
data class ExamsResult(
	val id: Long,
	val type: ElementType,
	val exams: List<Exam>
)

package com.sapuseven.untis.core.api.mobile.model.response

import com.sapuseven.untis.core.api.mobile.model.untis.classreg.HomeWork
import com.sapuseven.untis.core.api.mobile.model.untis.timetable.Lesson
import kotlinx.serialization.Serializable

@Serializable
data class HomeworkResponse(
		val result: HomeworkResult? = null
) : BaseResponse()

@Serializable
data class HomeworkResult(
		val homeWorks: List<HomeWork>,
		val lessonsById: Map<Long, Lesson>
)

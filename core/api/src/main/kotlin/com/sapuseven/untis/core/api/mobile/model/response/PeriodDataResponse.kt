package com.sapuseven.untis.core.api.mobile.model.response

import com.sapuseven.untis.core.api.mobile.model.untis.MasterData
import com.sapuseven.untis.core.api.mobile.model.untis.Person
import com.sapuseven.untis.core.api.mobile.model.untis.Timetable
import com.sapuseven.untis.core.api.mobile.model.untis.timetable.PeriodData
import kotlinx.serialization.Serializable

@Serializable
data class PeriodDataResponse(
		val result: PeriodDataResult? = null
) : BaseResponse()

@Serializable
data class PeriodDataResult(
	val dataByTTId: Map<Long, PeriodData>,
	val referencedStudents: List<Person>
)

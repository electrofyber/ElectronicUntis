package com.sapuseven.untis.core.api.mobile.model.response

import com.sapuseven.untis.core.api.mobile.model.untis.MasterData
import com.sapuseven.untis.core.api.mobile.model.untis.Timetable
import kotlinx.serialization.Serializable

@Serializable
data class TimetableResponse(
		val result: TimetableResult? = null
) : BaseResponse()

@Serializable
data class TimetableResult(
	val timetable: Timetable,
	val masterData: MasterData
)

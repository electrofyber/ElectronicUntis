package com.sapuseven.untis.core.api.mobile.model.response

import com.sapuseven.untis.core.api.mobile.model.untis.SchoolInfo
import kotlinx.serialization.Serializable

@Serializable
data class SchoolSearchResponse(
		val result: SchoolSearchResult? = null
) : BaseResponse()

@Serializable
data class SchoolSearchResult(
		val size: Int,
		val schools: List<SchoolInfo>
)

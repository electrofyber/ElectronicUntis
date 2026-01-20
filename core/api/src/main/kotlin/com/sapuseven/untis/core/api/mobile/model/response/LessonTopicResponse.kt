package com.sapuseven.untis.core.api.mobile.model.response

import kotlinx.serialization.Serializable

@Serializable
data class SubmitLessonTopicResponse(
	val result: SubmitLessonTopicResult? = null
) : BaseResponse()

@Serializable
data class SubmitLessonTopicResult(
	val success: Boolean
)

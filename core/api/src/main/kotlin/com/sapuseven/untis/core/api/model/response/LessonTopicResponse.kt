package com.sapuseven.untis.core.api.model.response

import kotlinx.serialization.Serializable

@Serializable
data class SubmitLessonTopicResponse(
	val result: SubmitLessonTopicResult? = null
) : BaseResponse()

@Serializable
data class SubmitLessonTopicResult(
	val success: Boolean
)

package com.sapuseven.untis.core.api.model.request

import com.sapuseven.untis.core.api.model.untis.Auth
import kotlinx.serialization.Serializable

@Serializable
data class SubmitLessonTopicParams(
	val ttId: Long,
	val lessonTopic: String,
	val auth: Auth
) : BaseParams()

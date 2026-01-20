package com.sapuseven.untis.core.api.mobile.model.response

import com.sapuseven.untis.core.api.mobile.model.untis.MessageOfDay
import kotlinx.serialization.Serializable

@Serializable
data class MessagesOfDayResponse(
		val result: MessagesOfDayResult? = null
) : BaseResponse()

@Serializable
data class MessagesOfDayResult(
		val messages: List<MessageOfDay>
)

package com.sapuseven.untis.core.api.model.untis.messenger

import kotlinx.serialization.Serializable

@Serializable
data class MessengerSettings(
	val serverUrl: String,
	val organizationId: String
)

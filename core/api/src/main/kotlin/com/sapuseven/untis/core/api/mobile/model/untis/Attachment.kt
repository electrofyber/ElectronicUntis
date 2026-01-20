package com.sapuseven.untis.core.api.mobile.model.untis

import kotlinx.serialization.Serializable

@Serializable
data class Attachment(
	val id: Long,
	val name: String,
	val url: String
)

package com.sapuseven.untis.core.api.mobile.model.response

import kotlinx.serialization.Serializable

@Serializable
data class AppSharedSecretResponse(
		val result: String? = null
) : BaseResponse()

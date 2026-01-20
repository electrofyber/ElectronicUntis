package com.sapuseven.untis.core.api.mobile.model.response

import kotlinx.serialization.Serializable

@Serializable
data class AuthTokenResponse(
		val result: AuthTokenResult? = null
) : BaseResponse()

@Serializable
data class AuthTokenResult(
	val token: String
)

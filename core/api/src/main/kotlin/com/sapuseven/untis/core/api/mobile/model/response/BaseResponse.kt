package com.sapuseven.untis.core.api.mobile.model.response

import kotlinx.serialization.Serializable

@Serializable
open class BaseResponse {
	val id: String? = null
	val jsonrpc: String? = null
	val error: Error? = null
}

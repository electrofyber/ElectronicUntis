package com.sapuseven.untis.core.domain.exception

data class LoginException(
	val type: Type,
	override val message: String? = null
) : Exception(message) {

	enum class Type {
		UNKNOWN,
		REQUIRE_2_FACTOR
		// extend when needed
	}
}

package com.sapuseven.untis.core.model

data class User(
	val id: Long,
	val displayName: String,
	val school: School,
	val user: String? = null,
	val key: String? = null,
	val anonymous: Boolean = false,
) {
}

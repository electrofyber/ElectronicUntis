package com.sapuseven.untis.core.model

data class School(
	val name: String,
	val displayName: String,
	val address: String? = null,
	val apiUrl: String,
)

package com.sapuseven.untis.core.model.timetable

data class School(
	val name: String,
	val displayName: String,
	val address: String? = null,
	val api: SchoolApi,
)

data class SchoolApi(
	val base: String,
	val jsonRpc: String,
	val rest: String,
	val restAuth: String,
)

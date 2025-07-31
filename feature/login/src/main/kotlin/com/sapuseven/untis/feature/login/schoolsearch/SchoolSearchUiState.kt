package com.sapuseven.untis.feature.login.schoolsearch

import com.sapuseven.untis.core.model.School

data class SchoolSearchUiState(
	val results: List<School> = emptyList(),
	val errorResId: Int? = null,
	val errorRaw: String? = null,
	val isLoading: Boolean = false,
)

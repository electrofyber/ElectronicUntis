package com.sapuseven.untis.core.domain.repository

import com.sapuseven.untis.core.model.timetable.School

interface SchoolRepository {
	suspend fun searchSchool(
		schoolName: String
	): Result<School>

	suspend fun searchSchools(
		schoolName: String
	): Result<List<School>>
}

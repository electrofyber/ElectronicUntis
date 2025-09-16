package com.sapuseven.untis.core.data.repository

import com.sapuseven.untis.core.api.client.SchoolSearchApi
import com.sapuseven.untis.core.api.model.untis.SchoolInfo
import com.sapuseven.untis.core.data.mapper.toDomain
import com.sapuseven.untis.core.domain.repository.SchoolRepository
import com.sapuseven.untis.core.model.timetable.School
import javax.inject.Inject

class UntisSchoolRepository @Inject constructor(
	private val api: SchoolSearchApi
) : SchoolRepository {
	override suspend fun searchSchool(schoolName: String): Result<School> {
		return runCatching {
			val schoolId = schoolName.toLongOrNull()

			val schoolSearchResult = schoolId?.let {
				api.searchSchools(schoolId = it)
			} ?: api.searchSchools(schoolName = schoolName)

			if (schoolSearchResult.size == 1) schoolSearchResult.schools.first().toDomain()
			else {
				// Usually, there is only one result, but if there are multiple, we try to find the one that matches the loginName or schoolId
				schoolSearchResult.schools.find { schoolInfoResult ->
					schoolInfoResult.schoolId == schoolId || schoolInfoResult.loginName.equals(
						schoolName,
						true
					)
				}?.toDomain() ?: error("No matching school found for '$schoolName'")
			}
		}
	}

	override suspend fun searchSchools(schoolName: String): Result<List<School>> {
		return runCatching {
			val schoolSearchResult = api.searchSchools(search = schoolName)

			schoolSearchResult.schools.map( SchoolInfo::toDomain)
		}
	}
}

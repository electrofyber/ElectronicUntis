package com.sapuseven.untis.core.data.repository

import com.sapuseven.untis.core.data.mapper.toDomain
import com.sapuseven.untis.core.database.dao.SchoolYearDao
import com.sapuseven.untis.core.domain.repository.SchoolYearRepository
import com.sapuseven.untis.core.model.masterdata.SchoolYear
import com.sapuseven.untis.core.model.user.User
import javax.inject.Inject

class UntisSchoolYearRepository @Inject constructor(
	private val schoolYearDao: SchoolYearDao
) : SchoolYearRepository {

	override suspend fun getSchoolYearsForUser(user: User): List<SchoolYear> {
		return schoolYearDao.getByUserId(user.id).map { it.toDomain() }
	}
}

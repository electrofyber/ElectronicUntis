package com.sapuseven.untis.core.domain.repository

import com.sapuseven.untis.core.model.masterdata.SchoolYear
import com.sapuseven.untis.core.model.user.User

interface SchoolYearRepository {
	suspend fun getSchoolYearsForUser(user: User): List<SchoolYear>
}

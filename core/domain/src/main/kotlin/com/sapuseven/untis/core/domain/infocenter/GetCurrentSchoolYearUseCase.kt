package com.sapuseven.untis.core.domain.infocenter

import com.sapuseven.untis.core.domain.repository.SchoolYearRepository
import com.sapuseven.untis.core.model.masterdata.SchoolYear
import com.sapuseven.untis.core.model.user.User
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import javax.inject.Inject

class GetCurrentSchoolYearUseCase @Inject constructor(
	private val schoolYearRepository: SchoolYearRepository,
	private val clock: Clock = Clock.System,
	private val zone: TimeZone = TimeZone.currentSystemDefault(),
) {
	suspend operator fun invoke(user: User, currentDate: LocalDate = clock.todayIn(zone)): SchoolYear? {
		return schoolYearRepository.getSchoolYearsForUser(user).find {
			currentDate in it.startDate..it.endDate
		}
	}
}

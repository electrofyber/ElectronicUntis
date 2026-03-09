package com.sapuseven.untis.core.domain.infocenter

import com.sapuseven.untis.core.domain.repository.InfoCenterRepository
import com.sapuseven.untis.core.domain.repository.UserRepository
import com.sapuseven.untis.core.model.officehours.OfficeHour
import com.sapuseven.untis.core.model.user.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import javax.inject.Inject

class GetOfficeHoursUseCase @Inject constructor(
	private val infoCenterRepository: InfoCenterRepository,
	private val clock: Clock = Clock.System,
	private val zone: TimeZone = TimeZone.currentSystemDefault(),
) {
	operator fun invoke(user: User): Flow<Result<List<OfficeHour>>> = infoCenterRepository
		.getOfficeHours(
			user,
			InfoCenterRepository.OfficeHoursParams(-1, clock.todayIn(zone)),
		)
		.map(Result.Companion::success)
		.catch { emit(Result.failure(it)) }
}

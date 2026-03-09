package com.sapuseven.untis.core.domain.infocenter

import com.sapuseven.untis.core.domain.repository.InfoCenterRepository
import com.sapuseven.untis.core.domain.repository.UserRepository
import com.sapuseven.untis.core.model.messages.MessageOfDay
import com.sapuseven.untis.core.model.user.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import java.time.LocalDate
import javax.inject.Inject

class GetMessagesOfDayUseCase @Inject constructor(
	userRepository: UserRepository,
	private val infoCenterRepository: InfoCenterRepository,
	private val clock: Clock = Clock.System,
	private val zone: TimeZone = TimeZone.currentSystemDefault(),
) {
	operator fun invoke(user: User): Flow<Result<List<MessageOfDay>>> = infoCenterRepository
		.getMessagesOfDay(user, clock.todayIn(zone))
		.map(Result.Companion::success)
		.catch { emit(Result.failure(it)) }
}

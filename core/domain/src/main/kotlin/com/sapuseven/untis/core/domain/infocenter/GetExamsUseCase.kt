package com.sapuseven.untis.core.domain.infocenter

import com.sapuseven.untis.core.domain.repository.InfoCenterRepository
import com.sapuseven.untis.core.model.timetable.ElementType
import com.sapuseven.untis.core.model.timetable.Exam
import com.sapuseven.untis.core.model.user.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import javax.inject.Inject

class GetExamsUseCase @Inject constructor(
	private val infoCenterRepository: InfoCenterRepository,
	private val getCurrentSchoolYear: GetCurrentSchoolYearUseCase,
	private val clock: Clock = Clock.System,
	private val zone: TimeZone = TimeZone.currentSystemDefault(),
) {
	suspend operator fun invoke(user: User): Flow<Result<List<Exam>>> = getCurrentSchoolYear(user).let { currentSchoolYear ->
		val today = clock.todayIn(zone)

		infoCenterRepository.getExams(
			user,
			InfoCenterRepository.EventsParams(
				user.element?.id ?: 0,
				user.element?.type ?: ElementType.STUDENT,
				today,
				currentSchoolYear?.endDate ?: today,
			)
		)
			.map(Result.Companion::success)
			.catch { emit(Result.failure(it)) }
	}
}


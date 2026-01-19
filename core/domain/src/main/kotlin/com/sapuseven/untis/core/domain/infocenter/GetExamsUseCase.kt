package com.sapuseven.untis.core.domain.infocenter

import com.sapuseven.untis.core.domain.repository.InfoCenterRepository
import com.sapuseven.untis.core.domain.repository.UserRepository
import com.sapuseven.untis.core.model.timetable.ElementType
import com.sapuseven.untis.core.model.timetable.Exam
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import javax.inject.Inject


class GetExamsUseCase @Inject constructor(
	userRepository: UserRepository,
	private val infoCenterRepository: InfoCenterRepository,
	private val clock: Clock = Clock.System,
	private val zone: TimeZone = TimeZone.currentSystemDefault(),
	getCurrentSchoolYear: GetCurrentSchoolYearUseCase,
) {
	private val currentUser = userRepository.getActiveUser()

	private val currentSchoolYear =
		getCurrentSchoolYear() ?: 0/*SchoolYearEntity(
			startDate = LocalDate.now(),
			endDate = LocalDate.now()
		)*/

	operator fun invoke(): Flow<Result<List<Exam>>> = infoCenterRepository.getExams(
		currentUser,
		InfoCenterRepository.EventsParams(
			currentUser.element?.id ?: 0,
			currentUser.element?.type ?: ElementType.STUDENT,
			clock.todayIn(zone),
			clock.todayIn(zone),
			//currentSchoolYear.endDate
		),
	)
		.map(Result.Companion::success)
		.catch { emit(Result.failure(it)) }
}

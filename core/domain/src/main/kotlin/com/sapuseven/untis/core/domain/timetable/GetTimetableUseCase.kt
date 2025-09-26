package com.sapuseven.untis.core.domain.timetable

import com.sapuseven.untis.core.domain.cache.FromCache
import com.sapuseven.untis.core.domain.repository.TimetableRepository
import com.sapuseven.untis.core.model.timetable.Element
import com.sapuseven.untis.core.model.timetable.Timetable
import com.sapuseven.untis.core.model.user.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetTimetableUseCase @Inject constructor(
	private val timetableRepository: TimetableRepository,
	private val mergeTimetablePeriods: MergeTimetablePeriods,
	private val weekLogicService: WeekLogicService,
) {
	operator fun invoke(
		user: User,
		element: Element,
		page: Int,
		fromCache: FromCache
	): Flow<Timetable> {
		val (startDate, endDate) = weekLogicService.dateRangeForPageIndex(page)

		return timetableRepository.getTimetable(
			user,
			TimetableRepository.TimetableParams(
				elementId = element.id,
				elementType = element.type,
				startDate = startDate,
				endDate = endDate
			),
			fromCache
		).map {
			// TODO: Filter according to hideCancelled
			it.copy(periods = mergeTimetablePeriods(it.periods, user.timeGrid.days))
		}
	}
}

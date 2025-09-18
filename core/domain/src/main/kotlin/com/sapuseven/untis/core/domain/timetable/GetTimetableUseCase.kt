package com.sapuseven.untis.core.domain.timetable

import com.sapuseven.untis.core.domain.cache.FromCache
import com.sapuseven.untis.core.domain.repository.TimetableRepository
import com.sapuseven.untis.core.model.timetable.Element
import com.sapuseven.untis.core.model.timetable.Timetable
import com.sapuseven.untis.core.model.user.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import javax.inject.Inject

class GetTimetableUseCase @Inject constructor(
	private val timetableRepository: TimetableRepository,
	private val mergeTimetablePeriods: MergeTimetablePeriods,
) {
	operator fun invoke(
		user: User,
		element: Element,
		startDate: LocalDate,
		endDate: LocalDate = startDate.plus(DatePeriod(days = 5 /*TODO*/)),
		fromCache: Boolean
	): Flow<Timetable> =
		timetableRepository.getTimetable(
			user,
			TimetableRepository.TimetableParams(
				elementId = element.id,
				elementType = element.type,
				startDate = startDate,
				endDate = endDate
			),
			if (fromCache) FromCache.CACHED_THEN_LOAD else FromCache.NEVER
		).map {
			// TODO: Filter according to hideCancelled
			it.copy(periods = mergeTimetablePeriods(it.periods, user.timeGrid.days))
		}
}

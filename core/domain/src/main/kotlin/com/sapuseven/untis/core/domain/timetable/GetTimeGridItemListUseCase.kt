package com.sapuseven.untis.core.domain.timetable

import com.sapuseven.untis.core.domain.repository.UserRepository
import com.sapuseven.untis.core.model.timetable.TimeGridItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetTimeGridItemListUseCase @Inject constructor(
	private val userRepository: UserRepository
) {
	operator fun invoke(): Flow<List<TimeGridItem>> {
		return userRepository.observeActiveUser().map { user ->
			user?.run { timeGrid.days.flatMap { day -> day.units.map { TimeGridItem(day, it) } } } ?: emptyList()
		}
	}
}

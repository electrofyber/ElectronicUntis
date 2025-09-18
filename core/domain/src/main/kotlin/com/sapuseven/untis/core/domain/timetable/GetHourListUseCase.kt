package com.sapuseven.untis.core.domain.timetable

import com.sapuseven.compose.protostore.ui.preferences.convertRangeToPair
import com.sapuseven.untis.core.datastore.UserSettingsDataSource
import com.sapuseven.untis.core.domain.repository.UserRepository
import com.sapuseven.untis.core.model.timetable.WeekViewHour
import com.sapuseven.untis.core.model.user.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetHourListUseCase @Inject constructor(
	private val userRepository: UserRepository,
	private val userSettingsDataSource: UserSettingsDataSource
) {
	operator fun invoke(): Flow<List<WeekViewHour>> {
		return userSettingsDataSource.getSettings().map { settings ->
			buildHourList(
				user = userRepository.getActiveUser(),
				range = settings.timetableRange.convertRangeToPair(),
				rangeIndexReset = settings.timetableRangeIndexReset
			)
		}
	}

	private fun buildHourList(
		user: User, range: Pair<Int, Int>?, rangeIndexReset: Boolean
	): List<WeekViewHour> = user.timeGrid.days
		.maxByOrNull { it.units.size }
		?.units
		?.mapIndexedNotNull { index, hour ->
			// Check if outside configured range
			if (range != null && index !in range.first - 1 until range.second) return@mapIndexedNotNull null

			// If label is empty, fill it according to preferences
			val label = if (rangeIndexReset) {
				(index + 2 - (range?.first ?: 1)).toString()
			} else {
				hour.label.ifEmpty { (index + 1).toString() }
			}

			WeekViewHour(hour.startTime, hour.endTime, label)
		}.orEmpty()
}

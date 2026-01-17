package com.sapuseven.untis.core.domain.roomfinder

import com.sapuseven.untis.core.domain.cache.FromCache
import com.sapuseven.untis.core.domain.repository.RoomFinderRepository
import com.sapuseven.untis.core.domain.repository.TimetableRepository
import com.sapuseven.untis.core.domain.repository.UserRepository
import com.sapuseven.untis.core.domain.timetable.WeekLogicService
import com.sapuseven.untis.core.model.roomfinder.RoomFinderItem
import com.sapuseven.untis.core.model.roomfinder.RoomFinderItemData
import com.sapuseven.untis.core.model.timetable.ElementType
import com.sapuseven.untis.core.model.timetable.PeriodState
import com.sapuseven.untis.core.model.timetable.Timetable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetRoomFinderItemsUseCase @Inject constructor(
	userRepository: UserRepository,
	private val roomFinderRepository: RoomFinderRepository,
	private val timetableRepository: TimetableRepository,
	private val weekLogicService: WeekLogicService,
) {
	companion object {
		private const val ONE_HOUR: Long = 60 * 60 * 1000
	}

	private val roomData: MutableMap<RoomFinderItem, List<Boolean>> = mutableMapOf()

	private val user = userRepository.getActiveUser()

	@OptIn(ExperimentalCoroutinesApi::class)
	operator fun invoke(): Flow<List<RoomFinderItemData>> = roomFinderRepository
		.observeAllRooms(user.id)
		.flatMapLatest { rooms ->
			flow {
				emitRoomData(rooms)

				rooms.forEach { roomEntity ->
					fetchRoomStates(roomEntity).collect {
						roomData[roomEntity] = it
						emitRoomData(rooms)
					}
				}
			}
		}

	private suspend fun FlowCollector<List<RoomFinderItemData>>.emitRoomData(rooms: List<RoomFinderItem>) {
		emit(rooms.map { room ->
			RoomFinderItemData(room, roomData.getOrDefault(room, emptyList()))
		})
	}

	private fun fetchRoomStates(room: RoomFinderItem): Flow<List<Boolean>> {
		val (startDate, endDate) = weekLogicService.dateRangeForPageIndex(0)

		return timetableRepository.getTimetable(
			user = user,
			params = TimetableRepository.TimetableParams(
				room.elementId,
				ElementType.ROOM,
				startDate,
				endDate
			),
			fromCache = FromCache.IF_HAVE, // Use a conservative caching strategy to reduce API request count
			maxAge = ONE_HOUR
		).map(::mapTimetableToBooleanList)
	}

	private fun mapTimetableToBooleanList(timetable: Timetable): List<Boolean> {
		return user.timeGrid.days.flatMap { day ->
			day.units.map { unit ->
				timetable.periods.any { period ->
					!period.states.contains(PeriodState.CANCELLED) &&
						period.startDateTime.dayOfWeek == day.dayOfWeek &&
						unit.endTime > period.startDateTime.time &&
						unit.startTime < period.endDateTime.time
				}
			}
		}
	}
}

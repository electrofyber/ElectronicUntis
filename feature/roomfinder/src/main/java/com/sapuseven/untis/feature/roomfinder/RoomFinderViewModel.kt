package com.sapuseven.untis.feature.roomfinder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sapuseven.untis.core.domain.repository.MasterDataRepository
import com.sapuseven.untis.core.domain.roomfinder.AddRoomFinderItemUseCase
import com.sapuseven.untis.core.domain.roomfinder.DeleteRoomFinderItemUseCase
import com.sapuseven.untis.core.domain.roomfinder.GetRoomFinderItemsUseCase
import com.sapuseven.untis.core.domain.timetable.GetTimeGridItemListUseCase
import com.sapuseven.untis.core.model.roomfinder.RoomFinderItem
import com.sapuseven.untis.core.model.roomfinder.RoomFinderItemData
import com.sapuseven.untis.core.model.timetable.Element
import com.sapuseven.untis.core.model.timetable.ElementType
import com.sapuseven.untis.core.model.timetable.TimeGridItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject


@HiltViewModel
class RoomFinderViewModel @Inject constructor(
	clock: Clock,
	zone: TimeZone,
	masterDataRepository: MasterDataRepository,
	getHourListUseCase: GetTimeGridItemListUseCase,
	getRoomFinderItemsUseCase: GetRoomFinderItemsUseCase,
	private val addRoomFinderItemUseCase: AddRoomFinderItemUseCase,
	private val deleteRoomFinderItemUseCase: DeleteRoomFinderItemUseCase,
) : ViewModel() {
	private val _selectedHourIndex = MutableStateFlow(0)

	private val _elements: Flow<Map<ElementType, List<Element>>> =
		masterDataRepository.rooms.map { mapOf(ElementType.ROOM to it) } // TODO: Move to usecase (domain layer)

	private val _hourList: Flow<List<TimeGridItem>> = getHourListUseCase()
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5_000),
			initialValue = emptyList()
		)

	private val currentDateTime = clock.now().toLocalDateTime(zone)

	//private val _uiState = MutableStateFlow(RoomFinderUiState())
	val uiState: StateFlow<RoomFinderUiState> = combine(
		_elements,
		_hourList,
		getRoomFinderItemsUseCase(),
		_selectedHourIndex,
	) { elements, hourList, roomList, selectedHourIndex ->
		RoomFinderUiState(
			elements = elements,
			hourState = HourState(
				hours = hourList,
				selectedIndex = selectedHourIndex,
				hasPrev = selectedHourIndex > 0,
				hasNext = selectedHourIndex < hourList.lastIndex
			),
			roomList = sortRoomList(roomList, selectedHourIndex)
		)
	}.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), RoomFinderUiState())

	init {
		_hourList.filter { it.isNotEmpty() }.take(1)
			.onEach { hourList ->
				val initialIndex = hourList.indexOfFirst {
					it.day.dayOfWeek == currentDateTime.dayOfWeek && currentDateTime.time < it.unit.endTime
				}.coerceAtLeast(0)
				_selectedHourIndex.value = initialIndex
			}
			.launchIn(viewModelScope)
	}

	private fun sortRoomList(roomList: List<RoomFinderItemData>, selectedIndex: Int) =
		roomList.sortedWith(
			compareByDescending<RoomFinderItemData> { it
				.freeHoursAt(selectedIndex) }
				.thenBy { uiState.value.elements[ElementType.ROOM]?.find { element -> element.id == it.room.elementId }?.longName }
		)

	fun addRooms(rooms: List<Element>) = viewModelScope.launch {
		addRoomFinderItemUseCase(rooms)
	}

	fun deleteRoom(room: RoomFinderItem) = viewModelScope.launch {
		deleteRoomFinderItemUseCase(room)
	}

	fun selectHour(hourIndex: Int?) {
		hourIndex?.let { _selectedHourIndex.value = it }
	}
}

data class HourState(
	val hours: List<TimeGridItem> = emptyList(),
	val selectedIndex: Int = 0,
	val hasPrev: Boolean = false,
	val hasNext: Boolean = false
) {
	val selected
		get() = hours[selectedIndex]
}

data class RoomFinderUiState(
	val elements: Map<ElementType, List<Element>> = emptyMap(),
	val roomList: List<RoomFinderItemData> = emptyList(),
	val hourState: HourState = HourState(),
)

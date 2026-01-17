package com.sapuseven.untis.core.domain.roomfinder

import com.sapuseven.untis.core.domain.repository.RoomFinderRepository
import com.sapuseven.untis.core.domain.repository.UserRepository
import com.sapuseven.untis.core.model.roomfinder.RoomFinderItem
import com.sapuseven.untis.core.model.timetable.Element
import javax.inject.Inject

class AddRoomFinderItemUseCase @Inject constructor(
	private val roomFinderRepo: RoomFinderRepository,
	private val userRepo: UserRepository
) {
	suspend operator fun invoke(rooms: List<Element>) {
		roomFinderRepo.insertAll(rooms.map { RoomFinderItem(it.id, userRepo.getActiveUser().id) })
	}
}

package com.sapuseven.untis.core.domain.roomfinder

import com.sapuseven.untis.core.domain.repository.RoomFinderRepository
import com.sapuseven.untis.core.model.roomfinder.RoomFinderItem
import javax.inject.Inject

class DeleteRoomFinderItemUseCase @Inject constructor(
	private val roomFinderRepo: RoomFinderRepository
) {
	suspend operator fun invoke(room: RoomFinderItem) {
		roomFinderRepo.delete(room)
	}
}

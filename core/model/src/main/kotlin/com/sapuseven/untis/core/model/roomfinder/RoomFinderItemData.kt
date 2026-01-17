package com.sapuseven.untis.core.model.roomfinder

import kotlinx.serialization.Serializable


@Serializable
data class RoomFinderItemData(
	val room: RoomFinderItem,
	val states: List<Boolean>
) {
	fun freeHoursAt(hourIndex: Int): Int {
		return states.drop(hourIndex).takeWhile { !it }.count()
	}
}

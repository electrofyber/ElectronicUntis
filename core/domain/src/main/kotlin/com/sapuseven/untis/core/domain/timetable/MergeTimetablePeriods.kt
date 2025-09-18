package com.sapuseven.untis.core.domain.timetable

import com.sapuseven.untis.core.model.timetable.Period
import com.sapuseven.untis.core.model.timetable.TimeGridDay
import javax.inject.Inject

class MergeTimetablePeriods @Inject constructor() {
	operator fun invoke(periods: List<Period>, days: List<TimeGridDay>): List<Period> {
		val itemGrid: Array<Array<MutableList<Period>>> =
			Array(days.size) { Array(days.maxByOrNull { it.units.size }!!.units.size) { mutableListOf() } }
		val leftover: MutableList<Period> = mutableListOf()

		// Put all items into a two dimensional array depending on day and hour
		periods.forEach { item ->
			val day = item.endDateTime.dayOfWeek.value - days.first().dayOfWeek.value

			if (day < 0 || day >= days.size) return@forEach

			val thisUnitStartIndex = days[day].units.indexOfFirst {
				it.startTime == item.startDateTime.time
			}

			val thisUnitEndIndex = days[day].units.indexOfFirst {
				it.endTime == item.endDateTime.time
			}

			if (thisUnitStartIndex != -1 && thisUnitEndIndex != -1) itemGrid[day][thisUnitStartIndex].add(
				item
			)
			else leftover.add(item)
		}

		val newItems = itemGrid.flatMap { unitsOfDay ->
			unitsOfDay.flatMapIndexed { unitIndex, items ->
				items.onEach {
					var i = 1
					while (unitIndex + i < unitsOfDay.size && it.mergeWith(unitsOfDay[unitIndex + i])) i++
				}
			}
		}.toMutableList()

		newItems.addAll(leftover) // Add items that didn't fit inside the timegrid. These will always be single lessons.

		return newItems
	}

	private fun Period.mergeWith(items: MutableList<Period>): Boolean {
		for (i in items.indices.reversed()) {
			val candidate = items[i]

			if (candidate.startDateTime.dayOfYear != startDateTime.dayOfYear) continue

			if (this.equalsIgnoreTime(candidate)) {
				endDateTime = candidate.endDateTime
				items.removeAt(i)
				return true
			}
		}
		return false
	}
}
